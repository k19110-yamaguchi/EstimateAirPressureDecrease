package com.example.estimateairpressuredecrease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.entities.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val homeDao: HomeDao) : ViewModel() {
    var isFirstTime by mutableStateOf(true)
    var textStatus by mutableStateOf("学習状態")
    var isTrainingState by mutableStateOf(true)
    var textMinProperPressure by mutableStateOf("")
    var minProperPressure by mutableStateOf(0)
    var inflatedDate: LocalDateTime by mutableStateOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0))

    val home = homeDao.getHomeData().distinctUntilChanged()

    fun createHome() {
        viewModelScope.launch {
            val newHome = Home(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.createHomeDB(newHome)
        }
    }

    // _isTrainingState: Boolean = isTrainingState, _minProperPressure: Int = minProperPressure, _inflatedDate: LocalDateTime = inflatedDate
    // val newHome = Home(id = id, isTrainingState = _isTrainingState, minProperPressure = _minProperPressure, inflatedDate = _inflatedDate)
    fun updateHome() {
        viewModelScope.launch {
            val newHome = Home(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }



}