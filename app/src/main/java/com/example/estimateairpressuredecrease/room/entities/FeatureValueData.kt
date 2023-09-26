package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeatureValueData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 加速度標準偏差
    var accSd: Double,
    // 振幅スペクトル
    var ampSptList: List<Double>,
    // 空気圧
    var airPressure: Int,
)

