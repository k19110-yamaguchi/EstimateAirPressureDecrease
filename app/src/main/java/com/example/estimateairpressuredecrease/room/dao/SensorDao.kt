package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.estimateairpressuredecrease.room.entities.AccData
import com.example.estimateairpressuredecrease.room.entities.LocData
import com.example.estimateairpressuredecrease.room.entities.SensorData
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    // センサデータを挿入
    @Insert
    suspend fun insertSensor(sensor: SensorData)

    // 加速度データを挿入
    @Insert
    suspend fun insertAcc(acc: AccData)

    // 位置データを挿入
    @Insert
    suspend fun insertLoc(loc: LocData)

    // センサデータを取得
    @Query("SELECT * FROM SensorData")
    fun getSensorData(): Flow<List<SensorData>>

    // 加速度データを取得
    @Query("SELECT * FROM AccData")
    fun getAccData(): Flow<List<AccData>>

    // 位置データを取得
    @Query("SELECT * FROM LocData")
    fun getLocData(): Flow<List<LocData>>

    // センサデータを削除
    @Delete
    suspend fun deleteSensorData(sensor: SensorData)

    // 加速度データを削除
    @Delete
    suspend fun deleteAccData(acc: AccData)

    // 位置データを削除
    @Delete
    suspend fun deleteLocData(loc: LocData)


}