package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class SensorData (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 測定開始時間
    var startDate: LocalDateTime,
    // 測定終了時間
    var stopDate: LocalDateTime,
    // 測定時の空気圧(学習の場合は入力が必要)
    var airPressure: Int = 0,
    // 推定空気圧
    var estimatedAirPressure: Int = 0,
)
