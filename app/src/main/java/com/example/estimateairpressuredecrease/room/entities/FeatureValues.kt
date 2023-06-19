package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeatureValues(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var sensorsId: Int,
    var accSd: Double,
    var ampSptList: List<Double>,
)
