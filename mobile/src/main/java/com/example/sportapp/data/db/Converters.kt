package com.example.sportapp.data.db

import androidx.room.TypeConverter
import com.example.sportapp.data.model.SensorConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromSensorConfigList(value: List<SensorConfig>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SensorConfig>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSensorConfigList(value: String): List<SensorConfig> {
        val gson = Gson()
        val type = object : TypeToken<List<SensorConfig>>() {}.type
        return gson.fromJson(value, type)
    }
}
