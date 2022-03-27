package com.eddy.locationmacheteplugin.data.models

data class ResultWrapper(
    val points: List<LocationPointEntity> = emptyList(),
    val localConfig: LocalConfig = LocalConfig()
)
