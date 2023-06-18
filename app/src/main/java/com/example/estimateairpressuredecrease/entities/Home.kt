package com.example.estimateairpressuredecrease.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Home(
    @PrimaryKey(autoGenerate = true) val id: Int,
    // true: 学習状態, false: 推定状態
    var isTrainingStateL: Boolean,
    // 最小適正空気圧
    var minProperPressure: Int,
    // 空気を注入した日付
    var inflatedDate: LocalDateTime,

)

