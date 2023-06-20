package com.example.estimateairpressuredecrease.room.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// RoomでLocalDateTimeを使用できるように
// LocalDateTime <-> String("yyyy/MM/dd HH-mm-ss")に変換
class DateConverters {
    //書式を指定
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH-mm-ss")

    @TypeConverter
    fun stringToLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value, dateFormat)
    }

    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime): String {
        return value.format(dateFormat)
    }
}