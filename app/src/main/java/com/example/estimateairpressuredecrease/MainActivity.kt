package com.example.estimateairpressuredecrease


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.estimateairpressuredecrease.components.Sensor
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.ui.theme.EstimateAirPressureDecreaseTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    // Accelerationクラスを読み込む
    private lateinit var acc: Accelerometer
    private lateinit var  gps: Gps

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // パーミッションが許可された場合の処理
                setMainContent()
            } else {
                // パーミッションが拒否された場合の処理
                setMainContent(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationPermissionGranted = checkLocationPermission()

        if (locationPermissionGranted) {

            setMainContent()
        } else {
            setMainContent(false)
        }
    }

    private fun setMainContent(isPermitted: Boolean = true){
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(Color(0xFF654321))

            EstimateAirPressureDecreaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFAE29D)

                ) {
                    if(isPermitted){
                        MainContent(acc, gps)
                    }else{
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "位置情報の許可がないため", fontSize = 30.sp)
                            Text(text = "アプリを利用できません", fontSize = 30.sp)
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestLocationPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //
    override fun onResume() {
        super.onResume()
        acc = Accelerometer(this)
        gps = Gps(this)
    }

    //
    override fun onPause() {
        super.onPause()
        acc = Accelerometer(this)
        gps = Gps(this)
    }
}

@Composable
fun StatusBarColorSample() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Black)
    }
}

@Composable
fun MainContent(acc: Accelerometer, gps: Gps) {

    Sensor(acc, gps)
    //executionConfirmation()
}

