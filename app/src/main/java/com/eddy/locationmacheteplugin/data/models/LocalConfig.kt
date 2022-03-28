package com.eddy.locationmacheteplugin.data.models

data class LocalConfig(
    // location updates period in millis
    val periodicAccessTime: Long = 1000,
    // message to display on the notification
    val message: String = "New Content Near You, Check it out!!",
    // distance in meters to trigger
    val minTriggerDistance: Float = 100f
)
