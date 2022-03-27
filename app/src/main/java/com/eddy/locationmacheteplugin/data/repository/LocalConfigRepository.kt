package com.eddy.locationmacheteplugin.data.repository

import com.eddy.locationmacheteplugin.data.models.LocalConfig

interface LocalConfigRepository {
    operator fun invoke(): LocalConfig
}