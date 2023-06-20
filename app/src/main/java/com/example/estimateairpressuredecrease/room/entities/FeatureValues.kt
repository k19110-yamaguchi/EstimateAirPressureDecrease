package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeatureValues(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 特徴量を抽出するのに用いたセンサデータのid
    var sensorsId: Int,
    // 加速度標準偏差
    var accSd: Double,
    // 振幅スペクトル
    var ampSptList: List<Double>,
)
