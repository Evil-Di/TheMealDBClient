package ru.otusevildi.themealdbclient

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this) {
            if (!findNavController(R.id.secondary_container_view).popBackStack()) {
                if (!findNavController(R.id.main_container_view).popBackStack()) {
                    finish()
                }
            }
        }
    }
}