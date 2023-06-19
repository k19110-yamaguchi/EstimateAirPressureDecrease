package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var latList: List<Double>,
    var lonList: List<Double>,
    var timeList: List<Double>,

)
