package com.eddy.locationmacheteplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepositoryImpl
import com.eddy.locationmacheteplugin.service.LocationTrackingWorker

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val config = LocalConfigRepositoryImpl(filesDir).invoke()

        findViewById<Button>(R.id.button).setOnClickListener {
            LocationTrackingWorker.start(this, config)
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            LocationTrackingWorker.stop()
        }
    }
}