package com.example.stundenplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stundenplan.data.SelectionStore
import com.example.stundenplan.storage.SharedPreferencesStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = SelectionStore(SharedPreferencesStorage(this))
        setContent {
            App(store = store)
        }
    }
}
