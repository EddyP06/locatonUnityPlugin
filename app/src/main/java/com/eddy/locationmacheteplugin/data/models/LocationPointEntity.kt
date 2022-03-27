package com.eddy.locationmacheteplugin.data.models

import com.google.gson.annotations.SerializedName

data class LocationPointEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
