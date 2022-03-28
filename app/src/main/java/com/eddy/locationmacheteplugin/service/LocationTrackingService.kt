package com.eddy.locationmacheteplugin.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eddy.locationmacheteplugin.MainActivity2
import com.eddy.locationmacheteplugin.R
import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepository
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepositoryImpl
import com.eddy.locationmacheteplugin.data.repository.LocationAccessRepositoryImpl
import com.eddy.locationmacheteplugin.domain.DistanceUseCase
import com.eddy.locationmacheteplugin.domain.DistanceUseCaseImpl
import com.google.android.gms.location.*

class LocationTrackingService : Service() {

    private lateinit var localConfig: LocalConfigRepository

    private val distanceUseCase: DistanceUseCase by lazy {
        DistanceUseCaseImpl(
            LocationAccessRepositoryImpl(filesDir),
            localConfig
        )
    }

    private lateinit var locationClient: FusedLocationProviderClient

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location = p0.lastLocation.let { safeLocation ->
                return@let LocationPointEntity(
                    "",
                    safeLocation.latitude,
                    safeLocation.longitude
                )
            }
            var notificationCounter = 1000
            val (points, config) = distanceUseCase.invoke(location)

            val notificationManager: NotificationManagerCompat =
                NotificationManagerCompat.from(this@LocationTrackingService)
            points.forEach { item ->
                val builder = NotificationCompat.Builder(this@LocationTrackingService, CHANNEL_ID)
                    .setContentTitle("AR Trails")
                    .setContentText("${item.name} - ${config.message}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(notificationCounter++, builder.build())
            }

            val builder = NotificationCompat.Builder(this@LocationTrackingService, CHANNEL_ID)
                .setContentTitle("AR Trails")
                .setContentText("truluuu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.notify(notificationCounter++, builder.build())
        }
    }

    override fun onCreate() {
        super.onCreate()
        localConfig = LocalConfigRepositoryImpl(filesDir)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val forStop = intent?.getBooleanExtra(STOP_EXTRA, false) ?: false
        if (forStop) {
            stopTrackingLocation()
            stopForeground(true)
        } else {
            startForeground()
            startTrackingLocation()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity2::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AR Trails")
            .setContentText("We are searching for content near you!")
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "No location permission granted", Toast.LENGTH_SHORT).show()
            stopTrackingLocation()
            stopForeground(true)
            return
        }

        val locationRequest = LocationRequest.create().apply {
            val updatePeriod = localConfig.invoke().periodicAccessTime
            interval = updatePeriod
            fastestInterval = updatePeriod / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    private fun stopTrackingLocation() {
        // stop the tracking
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Content Reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "location_tracking_service"
        private const val CHANNEL_ID = "Content Reminder"
        private const val NOTIFICATION_ID = 56
        private const val STOP_EXTRA = "stop"

        fun stopService(context: Context) {
            Intent(context, LocationTrackingService::class.java).also { intent ->
                intent.putExtra(STOP_EXTRA, true)
                context.startService(intent)
            }

        }

    }
}