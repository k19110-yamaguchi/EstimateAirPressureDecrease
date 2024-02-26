package com.example.estimateairpressuredecrease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.estimateairpressuredecrease.components.pemissions.CheckPermissions
import com.example.estimateairpressuredecrease.sensors.*
import com.example.estimateairpressuredecrease.ui.theme.EstimateAirPressureDecreaseTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    // センサを使用するクラスを読み込む
    private lateinit var acc: Accelerometer
    private lateinit var gra: Gravity
    private lateinit var loc: Location
    private lateinit var bar: Barometric

    // センサを使うのに必要
    companion object {
        lateinit var content: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = this
        setContent {
            // カラー関係
            val systemUiController = rememberSystemUiController()
            val background = colorResource(id = R.color.background)
            val element = colorResource(id = R.color.element)
            // システムバーの色
            systemUiController.setStatusBarColor(element)

            EstimateAirPressureDecreaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CheckPermissions(acc, gra, loc, bar)
                    }
                }
            }
        }
    }

    //
    override fun onResume() {
        super.onResume()
        acc = Accelerometer(this)
        gra = Gravity(this)
        loc = Location(this)
        bar = Barometric(this)
    }

    //
    override fun onPause() {
        super.onPause()
        acc = Accelerometer(this)
        gra = Gravity(this)
        loc = Location(this)
        bar = Barometric(this)
    }
}
