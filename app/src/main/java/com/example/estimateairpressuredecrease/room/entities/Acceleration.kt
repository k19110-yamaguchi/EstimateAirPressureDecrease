package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Acceleration(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // X軸の加速度
    var xAccList: List<Double>,
    // Y軸の加速度
    var yAccList: List<Double>,
    // X軸の加速度
    var zAccList: List<Double>,
    // 加速度を取得した時間
    var timeList: List<Double>,

)
