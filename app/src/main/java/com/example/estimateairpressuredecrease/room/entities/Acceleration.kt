package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Acceleration(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var xAccList: List<Double>,
    var yAccList: List<Double>,
    var zAccList: List<Double>,
    var timeList: List<Double>,

)
