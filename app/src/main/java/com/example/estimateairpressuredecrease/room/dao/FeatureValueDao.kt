package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.estimateairpressuredecrease.room.entities.FeatureValueData
import kotlinx.coroutines.flow.Flow

@Dao
interface FeatureValueDao {
    // 特徴量を挿入
    @Insert
    suspend fun insertFeatureValues(featureValue: FeatureValueData)

    // 特徴量を取得
    @Query("SELECT * FROM FeatureValueData")
    fun getFeatureValues(): Flow<List<FeatureValueData>>

    // センサデータを削除
    @Delete
    suspend fun deleteFeatureValues(featureValue: FeatureValueData)


}