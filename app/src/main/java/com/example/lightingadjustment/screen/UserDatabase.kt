package com.example.lightingadjustment.screen

import android.content.Context
import androidx.room.*
import android.util.Log

// 用户实体类
@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String
)

// DAO接口
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
}

// Room数据库
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "userdata" // 数据库名
                ).build().also { INSTANCE = it }
            }
        }
    }
}
