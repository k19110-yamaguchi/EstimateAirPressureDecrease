package com.example.estimateairpressuredecrease.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.estimateairpressuredecrease.entities.Acceleration
import com.example.estimateairpressuredecrease.entities.Location
import com.example.estimateairpressuredecrease.entities.Sensors
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorsDao {

    // センサデータを挿入
    @Insert
    suspend fun insertSensor(sensors: Sensors)

    // 加速度データを挿入
    @Insert
    suspend fun insertAcc(acc: Acceleration)

    // 位置データを挿入
    @Insert
    suspend fun insertLoc(loc: Location)

    // センサデータを取得
    @Query("SELECT * FROM Sensors")
    fun getSensorData(): Flow<List<Sensors>>

    // 加速度データを取得
    @Query("SELECT * FROM Acceleration")
    fun getAccData(): Flow<List<Acceleration>>

    // 位置データを取得
    @Query("SELECT * FROM Location")
    fun getLocData(): Flow<List<Location>>

    // センサデータを削除
    @Delete
    suspend fun deleteSensorData(sensors: Sensors)

    // 加速度データを削除
    @Delete
    suspend fun deleteAccData(acc: Acceleration)

    // 位置データを削除
    @Delete
    suspend fun deleteLocData(loc: Location)


}