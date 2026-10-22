package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.ColdStorageDatabase
import com.example.data.ColdStorageRepository
import com.example.ui.ColdStorageViewModel
import com.example.ui.ColdStorageViewModelFactory
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { ColdStorageDatabase.getDatabase(this) }
    private val repository by lazy { ColdStorageRepository(database.coldStorageDao()) }

    private val viewModel: ColdStorageViewModel by viewModels {
        ColdStorageViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
