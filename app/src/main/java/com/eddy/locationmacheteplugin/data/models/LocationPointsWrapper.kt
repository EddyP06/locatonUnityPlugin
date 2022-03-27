package com.eddy.locationmacheteplugin.data.models

import com.google.gson.annotations.SerializedName

data class LocationPointsWrapper(
    @SerializedName("points")
    val points: MutableList<LocationPointEntity> = mutableListOf()
)
