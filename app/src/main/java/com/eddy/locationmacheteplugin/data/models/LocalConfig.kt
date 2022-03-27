package com.eddy.locationmacheteplugin.data.models

data class LocalConfig(
    // location updates period
    val periodicAccessTime: Long = 5,
    // message to display on the notification
    val message: String = "New Content Near You, Check it out!!",
    // distance in meters to trigger
    val minTriggerDistance: Float = 100f
)
