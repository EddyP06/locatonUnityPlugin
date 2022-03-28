package com.eddy.locationmacheteplugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepositoryImpl
import com.eddy.locationmacheteplugin.service.LocationTrackingService

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val config = LocalConfigRepositoryImpl(filesDir).invoke()

        findViewById<Button>(R.id.button).setOnClickListener {
            Intent(this, LocationTrackingService::class.java).also { intent ->
                startService(intent)
            }
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            LocationTrackingService.stopService(this)
        }
    }
}