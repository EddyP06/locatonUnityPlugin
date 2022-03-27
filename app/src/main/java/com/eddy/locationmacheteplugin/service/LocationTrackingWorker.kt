package com.eddy.locationmacheteplugin.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.eddy.locationmacheteplugin.data.models.LocalConfig
import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepository
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepositoryImpl
import com.eddy.locationmacheteplugin.data.repository.LocationAccessRepositoryImpl
import com.eddy.locationmacheteplugin.domain.DistanceUseCase
import com.eddy.locationmacheteplugin.domain.DistanceUseCaseImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit


class LocationTrackingWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val localConfig: LocalConfigRepository = LocalConfigRepositoryImpl(context.filesDir)

    private val distanceUseCase: DistanceUseCase by lazy {
        DistanceUseCaseImpl(
            LocationAccessRepositoryImpl(context.filesDir),
            localConfig
        )
    }

    private val locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

//    private val locationCallback: LocationCallback = object : LocationCallback() {
//        override fun onLocationResult(p0: LocationResult?) {
//            super.onLocationResult(p0)
//            TODO("not yet implemented")
//        }
//    }

    override suspend fun doWork(): Result {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return Result.failure()
            }
            locationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val location = it.result?.let { safeLocation ->
                        return@let LocationPointEntity(
                            "",
                            safeLocation.latitude,
                            safeLocation.longitude
                        )
                    } ?: return@addOnCompleteListener
                    createNotificationChannel()
                    val notificationManager: NotificationManagerCompat =
                        NotificationManagerCompat.from(context)
                    var notificationCounter = 1000
                    val (points, config) = distanceUseCase.invoke(location)
                    points.forEach { item ->
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle("AR Trails")
                            .setContentText("${item.name} - ${config.message}")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        notificationManager.notify(notificationCounter++, builder.build())
                    }

                    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("AR Trails")
                        .setContentText("tiririr")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    notificationManager.notify(notificationCounter++, builder.build())

                }
            }
        } catch (e: Exception) {
            Log.d(WORKER_TAG, "doWork: failed to access last location")
        }
        return Result.success()
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
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val WORKER_TAG = "location_tracking_tag"
        private const val CHANNEL_ID = "Content Reminder"

        fun start(context: Context, config: LocalConfig) {
            val periodicWork =
                PeriodicWorkRequest.Builder(
                    LocationTrackingWorker::class.java,
                    config.periodicAccessTime,
                    TimeUnit.SECONDS
                )
                    .addTag(WORKER_TAG)
                    .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "Location",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWork
            )
        }

        fun stop() {
            WorkManager.getInstance().cancelAllWorkByTag(WORKER_TAG)
        }
    }
}