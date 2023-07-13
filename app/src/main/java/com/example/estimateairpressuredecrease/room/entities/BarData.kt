package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BarData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 気圧
    var barList: List<Double>,
    // 気圧を取得した時間
    var timeList: List<Double>,

)
