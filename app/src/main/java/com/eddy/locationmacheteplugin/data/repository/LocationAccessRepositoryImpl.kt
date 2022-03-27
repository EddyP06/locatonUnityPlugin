package com.eddy.locationmacheteplugin.data.repository

import com.eddy.locationmacheteplugin.data.models.LocationPointEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class LocationAccessRepositoryImpl(private val filesDir: File) : LocationAccessRepository {

    private val entityType = object : TypeToken<List<LocationPointEntity>>() {}.type
    private val gson: Gson = Gson()

    override fun invoke(): List<LocationPointEntity> {
        val notificationFile = File(filesDir, CONTENT_LOCATION_FILE_NAME)
        if (!notificationFile.exists()) {
            return emptyList()
        }
        val contentAsString = notificationFile.readText()
        return gson.fromJson(contentAsString, entityType)
    }

    companion object {
        const val CONTENT_LOCATION_FILE_NAME = "content_location_meta"
    }
}