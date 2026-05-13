package com.example.profletterai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.profletterai.ui.navigation.AppNavigation
import com.example.profletterai.ui.theme.ProfletterAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfletterAiTheme {
                AppNavigation()
            }
        }
    }
}
