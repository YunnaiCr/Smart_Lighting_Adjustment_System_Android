package com.example.lightingadjustment.screen

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

//select typeface
@Composable
fun loadCustomFont(): FontFamily {
    val context = LocalContext.current
    val typeface = remember {
        Typeface.createFromAsset(context.assets, "kaishu.ttf")
    }
    return FontFamily(typeface)
}
