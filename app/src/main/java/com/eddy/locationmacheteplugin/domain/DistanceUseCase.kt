package com.eddy.locationmacheteplugin.domain

import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.eddy.locationmacheteplugin.data.models.ResultWrapper

interface DistanceUseCase {
    operator fun invoke(
        currentLocationPoint: LocationPointEntity
    ): ResultWrapper
}