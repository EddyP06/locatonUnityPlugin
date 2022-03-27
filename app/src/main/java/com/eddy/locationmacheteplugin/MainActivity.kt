package com.eddy.locationmacheteplugin

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.eddy.locationmacheteplugin.R
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepositoryImpl
import com.eddy.locationmacheteplugin.service.LocationTrackingWorker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        val config = LocalConfigRepositoryImpl(filesDir).invoke()

        findViewById<Button>(R.id.button).setOnClickListener {
            LocationTrackingWorker.start(this, config)
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            LocationTrackingWorker.stop()
        }
    }
}