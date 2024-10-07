package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StableIntervalData(
    @PrimaryKey
    val id: Int = 0,
    // 安定区間を抽出するファイル名
    val siFileName: String,
    // 安定区間の開始時間
    val siStarTime: Double,
    // 安定区間の終了時間
    val siStopTime: Double,
    // 安定区間が取得できる適正内のデータ数
    val withinAvailableRouteCount: Int,
    // 安定区間が取得できる適正外のデータ数
    val outOfAvailableRouteCount: Int,
    // 使用できるセンサデータのファイル名
    val availableFileName : List<String>,

)
