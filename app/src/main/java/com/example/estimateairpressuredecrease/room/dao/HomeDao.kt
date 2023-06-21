package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.estimateairpressuredecrease.room.entities.HomeData
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Insert
    // 起動初期、空のデータベースを作成
    suspend fun createHomeDB(home: HomeData)

    // idが一致するデータを取得
    @Query("SELECT * FROM HomeData WHERE id = :id")
    suspend fun getHomeById(id: Int): HomeData?

    // データを取得
    @Query("SELECT * FROM HomeData")
    fun getHomeData(): Flow<List<HomeData>>

    // データを更新
    @Update
    suspend fun updateHomeData(home: HomeData)


}