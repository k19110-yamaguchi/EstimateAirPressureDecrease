package com.example.estimateairpressuredecrease.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.estimateairpressuredecrease.room.converters.DateConverter
import com.example.estimateairpressuredecrease.room.converters.ListConverter
import com.example.estimateairpressuredecrease.room.dao.FeatureValueDao
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorDao
import com.example.estimateairpressuredecrease.room.entities.*


@Database(entities = [HomeData::class, SensorData::class, AccData::class, LocData::class, FeatureValueData::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDao(): HomeDao
    abstract fun sensorDao(): SensorDao
    abstract fun featureValueDao(): FeatureValueDao

}