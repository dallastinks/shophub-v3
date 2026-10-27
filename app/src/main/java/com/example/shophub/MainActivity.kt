package com.example.shophub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.shophub.navigation.ShopHubApp
import com.example.shophub.ui.theme.ShopHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopHubTheme {
                ShopHubApp()
            }
        }
    }
}