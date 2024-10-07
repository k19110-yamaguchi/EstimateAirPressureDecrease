package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.estimateairpressuredecrease.room.entities.StableIntervalData
import kotlinx.coroutines.flow.Flow

@Dao
interface StableIntervalDao {
    @Insert
    // 起動初期、空のデータベースを作成
    suspend fun createStableIntervalDB(stableInterval: StableIntervalData)

    // idが一致するデータを取得
    @Query("SELECT * FROM StableIntervalData WHERE id = :id")
    suspend fun getStableIntervalById(id: Int): StableIntervalData?

    // データを取得
    @Query("SELECT * FROM StableIntervalData")
    fun getStableIntervalData(): Flow<List<StableIntervalData>>

    // データを更新
    @Update
    suspend fun updateStableIntervalData(stableInterval: StableIntervalData)

}