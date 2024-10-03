package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StableIntervalData(
    @PrimaryKey
    val id: Int = 0,
    // 安定区間の開始の緯度
    val siStartLat: Double,
    // 安定区間の開始の経度
    val siStartLon: Double,
    // 安定区間の終了の緯度
    val siStopLat: Double,
    // 安定区間の終了の経度
    val siStopLon: Double,
    // 安定区間が取得できる適正内のデータ数
    val withinAvailableRouteCount: Int,
    // 安定区間が取得できる適正外のデータ数
    val outOfAvailableRouteCount: Int,
)
