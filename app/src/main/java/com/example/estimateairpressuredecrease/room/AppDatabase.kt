package com.example.estimateairpressuredecrease.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.estimateairpressuredecrease.room.converters.DateConverters
import com.example.estimateairpressuredecrease.room.converters.ListConverters
import com.example.estimateairpressuredecrease.room.dao.FeatureValuesDao
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorsDao
import com.example.estimateairpressuredecrease.room.entities.*


@Database(entities = [Home::class, Sensors::class, Acceleration::class, Location::class, FeatureValues::class], version = 1, exportSchema = false)
@TypeConverters(ListConverters::class, DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
    abstract fun sensorDao(): SensorsDao
    abstract fun featureValuesDao(): FeatureValuesDao

}