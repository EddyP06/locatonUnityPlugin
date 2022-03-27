package com.eddy.locationmacheteplugin.data.repository

import com.eddy.locationmacheteplugin.data.models.LocationPointEntity

interface LocationAccessRepository {
    /**
     * try to fetch the location of the content stored in the file directory
     *
     * @return the current location of the device wrapped in the list of [LocationPointEntity] model
     */
    operator fun invoke(): List<LocationPointEntity>
}