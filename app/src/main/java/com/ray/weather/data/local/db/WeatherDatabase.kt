package com.ray.weather.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ray.weather.data.local.dao.TopicDao
import com.ray.weather.data.local.model.TopicEntity

@Database(
    entities = [
        TopicEntity::class
    ],
    version = 1,
    exportSchema = true,
)

abstract class WeatherDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
}
