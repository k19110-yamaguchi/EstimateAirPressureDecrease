package com.example.estimateairpressuredecrease

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.estimateairpressuredecrease.converters.DateConverters
import com.example.estimateairpressuredecrease.converters.ListConverters
import com.example.estimateairpressuredecrease.dao.FeatureValuesDao
import com.example.estimateairpressuredecrease.dao.HomeDao
import com.example.estimateairpressuredecrease.dao.SensorsDao
import com.example.estimateairpressuredecrease.entities.*


@Database(entities = [Home::class, Sensors::class, Acceleration::class, Location::class, FeatureValues::class], version = 1)
@TypeConverters(ListConverters::class, DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
    abstract fun sensorDao(): SensorsDao
    abstract fun featureValuesDao(): FeatureValuesDao

}