package com.eddy.locationmacheteplugin.data.repository

import com.eddy.locationmacheteplugin.data.models.LocalConfig
import com.google.gson.Gson
import java.io.File

class LocalConfigRepositoryImpl(private val filesDir: File) : LocalConfigRepository {

    private val gson: Gson = Gson()

    override fun invoke(): LocalConfig {
        val notificationFile = File(
            filesDir,
            LOCAL_CONFIG_FILE_NAME
        )
        if (!notificationFile.exists()) {
            return LocalConfig()
        }
        val contentAsString = notificationFile.readText()
        return gson.fromJson(contentAsString, LocalConfig::class.java)
    }

    companion object {
        private const val LOCAL_CONFIG_FILE_NAME = "local_config"
    }
}