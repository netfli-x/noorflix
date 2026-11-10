package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.NoorFlixApp
import com.example.ui.theme.NoorFlixTheme
import com.example.ui.viewmodel.NoorFlixViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoorFlixTheme {
                val viewModel: NoorFlixViewModel = viewModel()
                NoorFlixApp(viewModel = viewModel)
            }
        }
    }
}
