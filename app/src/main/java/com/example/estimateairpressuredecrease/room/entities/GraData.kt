package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GraData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // X軸の重力加速度
    var xGraList: List<Double>,
    // Y軸の重力加速度
    var yGraList: List<Double>,
    // X軸の重力加速度
    var zGraList: List<Double>,
    // 重力加速度を取得した時間
    var timeList: List<Double>,
)
