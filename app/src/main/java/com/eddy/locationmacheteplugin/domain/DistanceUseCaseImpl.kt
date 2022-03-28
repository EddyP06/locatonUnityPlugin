package com.eddy.locationmacheteplugin.domain

import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.eddy.locationmacheteplugin.data.models.ResultWrapper
import com.eddy.locationmacheteplugin.data.repository.LocalConfigRepository
import com.eddy.locationmacheteplugin.data.repository.LocationAccessRepository
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class DistanceUseCaseImpl(
    private val contentAccessRepository: LocationAccessRepository,
    private val localConfigRepository: LocalConfigRepository
) : DistanceUseCase {
    override fun invoke(
        currentLocationPoint: LocationPointEntity
    ): ResultWrapper {
        val contentPoints = contentAccessRepository.invoke()
        val config = localConfigRepository.invoke()
        val resultPointList = mutableListOf<LocationPointEntity>()
        contentPoints.forEach {
            val currentDistance = meterDistanceBetweenPoints(currentLocationPoint, it)
            if (currentDistance <= config.minTriggerDistance) {
                resultPointList.add(it)
            }
        }
        return ResultWrapper(resultPointList, config)
    }

    private fun meterDistanceBetweenPoints(
        firstLocation: LocationPointEntity,
        secondLocation: LocationPointEntity
    ): Double {
        val firstLat: Double = firstLocation.latitude
        val firstLng: Double = firstLocation.longitude
        val secondLat: Double = secondLocation.latitude
        val secondLng: Double = secondLocation.longitude
        val pk = (180f / Math.PI).toFloat()
        val a1 = firstLat / pk
        val a2 = firstLng / pk
        val b1 = secondLat / pk
        val b2 = secondLng / pk
        val t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2)
        val t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2)
        val t3 = sin(a1) * sin(b1)
        val tt = acos(t1 + t2 + t3)
        return 6366000 * tt
    }

}