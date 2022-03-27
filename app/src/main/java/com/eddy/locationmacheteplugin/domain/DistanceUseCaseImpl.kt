package com.eddy.locationmacheteplugin.domain

import com.eddy.locationmacheteplugin.data.models.LocalConfig
import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.eddy.locationmacheteplugin.data.models.ResultWrapper
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepository
import com.eddy.locationmacheteplugin.data.repository.LocationAccessRepository

class DistanceUseCaseImpl(
    private val contentAccessRepository: LocationAccessRepository,
    private val localConfigRepository: LocalConfigRepository
) : DistanceUseCase {
    override fun invoke(
        currentLocationPoint: LocationPointEntity
    ): ResultWrapper {
        return ResultWrapper()
    }

}