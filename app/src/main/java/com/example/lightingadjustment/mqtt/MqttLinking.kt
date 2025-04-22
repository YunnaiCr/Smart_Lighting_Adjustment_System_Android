package com.example.lightingadjustment.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLSocketFactory
import com.example.lightingadjustment.R
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.sync.withLock
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttException
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlinx.coroutines.sync.*

class MqttLinking(context: Context) {
    private val broker = "ssl://jfe2a84f.ala.cn-hangzhou.emqxsl.cn:8883"
    private val clientID = "Android-Client"
    private val username = "Yunnai"
    private val password = "azathoth"
    private val tag = "MQTT"

    private val pendingSubscriptions = mutableSetOf<String>()
    private val subscriptionCallbacks = mutableMapOf<String, (String) -> Unit>()



    // Create a MQTT carrier
    private val mqttClient: MqttAndroidClient = MqttAndroidClient(context, broker, clientID).apply {
        setCallback(object : MqttCallback {
            //
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val payload = message?.payload?.takeIf { it.isNotEmpty() }?.toString(Charsets.UTF_8)?: ""
                Log.d(tag, "Received: $payload")
                topic?.let { sub -> subscriptionCallbacks[sub]?.invoke(payload) }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e(tag, "Connection lost: ${cause?.message}.")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(tag, "Message sent successfully.")
            }
        })
    }

    // MQTT connection
    fun connect(context: Context, callback: () -> Unit){
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            userName = username
            password = this@MqttLinking.password.toCharArray()
            socketFactory = getSSLSocketFactory(context)
        }

        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(tag, "Connection successful.")
                retryPendingSubscriptions()
                callback()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(tag, "Connection failed: ${exception?.message}")
            }
        })
    }

    // Load SSL ca certificate
    private fun getSSLSocketFactory(context: Context): SSLSocketFactory {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val ca = context.resources.openRawResource(R.raw.emqxsl_ca).use { certificateFactory.generateCertificate(it) }

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

            return SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, SecureRandom())
            }.socketFactory
        }
        catch (e: Exception) {
            throw RuntimeException("Error creating SSL Socket Factory", e)
        }
    }

    // MQTT disconnection
    fun disconnect() {
        mqttClient.disconnect()
    }

    // MQTT | Send message to the corresponding topic subscriber
    fun sendMessage(topic: String, payload: String) {
        val message = MqttMessage(payload.toByteArray()).apply {
            qos = 1
        }
        mqttClient.publish(topic, message)
    }

    // Send data by any field
    suspend fun sendData(vararg fields: String, userPreferencesManager: UserPreferencesManager) {
        val data = userPreferencesManager.getUserPreferences(*fields)
        val jsonData = Gson().toJson(data)
        sendMessage("bedroom/lighting", jsonData)
    }

    // Update and send the data (one field temporary)
    suspend fun updateAndSend (mutex: Mutex, userPreferencesManager: UserPreferencesManager, field: String, data: Any? ) {
            when (field) {
                "brightness" -> {
                    var updateValue = data as? Float
                    mutex.withLock {
                        userPreferencesManager.updateUserPreferences(brightness = updateValue)
                        sendData("brightness", userPreferencesManager = userPreferencesManager)
                    }
                    Log.d("Brightness Control", "亮度已调整为 $updateValue")
                }

                "sceneMode" -> {
                    var updateValue = data as? String
                    mutex.withLock {
                        userPreferencesManager.updateUserPreferences(sceneMode = updateValue)
                        sendData("sceneMode", userPreferencesManager = userPreferencesManager)
                    }
                    Log.d("Scene Mode Selection", "情景模式已调整为 $updateValue")
                }

                "color" -> {
                    var updateValue = data as? String
                    mutex.withLock {
                        userPreferencesManager.updateUserPreferences(color = updateValue)
                        sendData("color", userPreferencesManager = userPreferencesManager)
                    }
                    Log.d("Color Selection", "颜色已调整为 $updateValue")
                }

                "operationMode" -> {
                    var updateValue = data as? String
                    mutex.withLock {
                        userPreferencesManager.updateUserPreferences(operationMode = updateValue)
                        sendData("operationMode", userPreferencesManager = userPreferencesManager)
                    }
                    Log.d("Operation Mode Selection", "操作模式已调整为 $updateValue")
                }
            }
    }

    // MQTT | Subscribe the required topic and add the failed subscriptions to pending list
    fun subscribe(topic: String, qos: Int = 1, callback: (String) -> Unit) {
        subscriptionCallbacks[topic] = callback

        if (mqttClient.isConnected) {
            try {
                mqttClient.subscribe(topic, qos)
            } catch (e: MqttException) {
                Log.e(tag, "Failed to subscribe to $topic: ${e.message}")
                pendingSubscriptions.add(topic)
            }
        }
        else {
            Log.w(tag, "MQTT not connected yet, adding $topic to pending list")
            pendingSubscriptions.add(topic)
        }
    }

    // MQTT | Resolve pending subscription list
    private fun retryPendingSubscriptions() {
        if (pendingSubscriptions.isNotEmpty()) {
            Log.d(tag, "Retrying pending subscriptions...")
            val iterator = pendingSubscriptions.iterator()
            while (iterator.hasNext()) {
                val topic = iterator.next()
                try {
                    mqttClient.subscribe(topic, 1)
                    Log.d(tag, "Successfully subscribed to $topic")
                    iterator.remove()
                } catch (e: MqttException) {
                    Log.e(tag, "Retry failed for $topic: ${e.message}")
                }
            }
        }
    }

    // Convert received Json data to Map
    suspend fun handleReceivedData(message: String, userPreferencesManager: UserPreferencesManager) {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val data = Gson().fromJson<Map<String, Any>>(message, type)
        Log.d(tag, "Json to Map, Handled data: $data")
        for ((key, value) in data) {
            when (key) {
                "brightness" -> userPreferencesManager.updateUserPreferences(brightness = (value as Double).toFloat())
                "color" -> userPreferencesManager.updateUserPreferences(color = value as String)
                "sceneMode" -> userPreferencesManager.updateUserPreferences(sceneMode = value as String)
                "operationMode" -> userPreferencesManager.updateUserPreferences(operationMode = value as String)
            }
        }
    }
}