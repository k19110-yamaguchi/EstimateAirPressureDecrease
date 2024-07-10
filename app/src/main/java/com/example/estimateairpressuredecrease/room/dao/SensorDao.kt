package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.estimateairpressuredecrease.room.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    // センサデータを挿入
    @Insert
    suspend fun insertSensorData(sensor: SensorData)

    // センサデータを取得
    @Query("SELECT * FROM SensorData")
    fun getSensorData(): Flow<List<SensorData>>

    // センサデータを削除
    @Delete
    suspend fun deleteSensorData(sensor: SensorData)



}