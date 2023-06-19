package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Home(
    @PrimaryKey val id: Int = 0,
    // true: 学習状態, false: 推定状態
    var isTrainingState: Boolean,
    // 最小適正空気圧
    var minProperPressure: Int,
    // 空気を注入した日付
    var inflatedDate: LocalDateTime,

)

