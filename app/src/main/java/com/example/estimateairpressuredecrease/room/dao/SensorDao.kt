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

    // 加速度データを挿入
    @Insert
    suspend fun insertAccData(acc: AccData)

    // 重力加速度データを挿入
    @Insert
    suspend fun insertGraData(gra: GraData)

    // 位置データを挿入
    @Insert
    suspend fun insertLocData(loc: LocData)

    // 気圧データを挿入
    @Insert
    suspend fun insertBarData(bar: BarData)

    // センサデータを取得
    @Query("SELECT * FROM SensorData")
    fun getSensorData(): Flow<List<SensorData>>

    // 加速度データを取得
    @Query("SELECT * FROM AccData")
    fun getAccData(): Flow<List<AccData>>

    // 位置データを取得
    @Query("SELECT * FROM GraData")
    fun getGraData(): Flow<List<GraData>>

    // 位置データを取得
    @Query("SELECT * FROM LocData")
    fun getLocData(): Flow<List<LocData>>

    // 気圧データを取得
    @Query("SELECT * FROM BarData")
    fun getBarData(): Flow<List<BarData>>


    // センサデータを削除
    @Delete
    suspend fun deleteSensorData(sensor: SensorData)

    // 加速度データを削除
    @Delete
    suspend fun deleteAccData(acc: AccData)

    // 重力加速度データを削除
    @Delete
    suspend fun deleteGraData(gra: GraData)

    // 位置データを削除
    @Delete
    suspend fun deleteLocData(loc: LocData)

    // 気圧データを削除
    @Delete
    suspend fun deleteBarData(bar: BarData)


}