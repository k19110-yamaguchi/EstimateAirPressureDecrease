package com.example.estimateairpressuredecrease.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverters {
    @TypeConverter
    fun stringToDoubleList(value: String): List<Double> {
        val listType = object : TypeToken<List<Double>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun doubleListToString(value: List<Double>): String {
        return Gson().toJson(value)
    }


}