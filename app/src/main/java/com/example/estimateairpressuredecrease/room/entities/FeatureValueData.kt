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
    // 実際の空気圧
    var sensingAirPressure: Int,
    // 推定した空気圧
    var estimatedAirPressure: Int,
    // 特徴量に使用した開始時刻
    var startGetFv: List<Double>,
    // 特徴量に使用した終了時刻
    var stopGetFv: List<Double>,
)

