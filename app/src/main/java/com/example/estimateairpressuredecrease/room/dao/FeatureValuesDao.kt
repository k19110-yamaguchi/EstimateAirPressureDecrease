package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.estimateairpressuredecrease.room.entities.FeatureValues
import kotlinx.coroutines.flow.Flow

@Dao
interface FeatureValuesDao {
    // 特徴量を挿入
    @Insert
    suspend fun insertFeatureValues(featureValues: FeatureValues)

    // 特徴量を取得
    @Query("SELECT * FROM FeatureValues")
    fun getFeatureValues(): Flow<List<FeatureValues>>

    // センサデータを削除
    @Delete
    suspend fun deleteFeatureValues(featureValues: FeatureValues)


}