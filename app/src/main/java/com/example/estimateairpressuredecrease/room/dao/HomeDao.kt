package com.example.estimateairpressuredecrease.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.estimateairpressuredecrease.room.entities.Home
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Insert
    // 起動初期、空のデータベースを作成
    suspend fun createHomeDB(home: Home)

    // idが一致するデータを取得
    @Query("SELECT * FROM Home WHERE id = :id")
    suspend fun getHomeById(id: Int): Home?

    // データを取得
    @Query("SELECT * FROM Home")
    fun getHomeData(): Flow<List<Home>>

    // データを更新
    @Update
    suspend fun updateHomeData(home: Home)


}