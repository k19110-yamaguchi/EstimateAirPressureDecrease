package com.example.estimateairpressuredecrease.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeatureValues(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var sensorsId: Int,
    var accSd: Double,
    var ampSptList: List<Double>,
)
