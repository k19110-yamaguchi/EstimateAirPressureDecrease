package com.example.estimateairpressuredecrease


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.estimateairpressuredecrease.components.Home
import com.example.estimateairpressuredecrease.components.Sensor
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.ui.theme.EstimateAirPressureDecreaseTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    // Accelerationクラスを読み込む
    private lateinit var acc: Accelerometer
    private lateinit var gps: Gps
    private lateinit var gra: Gravity
    private lateinit var bar: Barometric
    companion object {
        lateinit var instance: MainActivity
    }

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
        val locationPermissionGranted by mutableStateOf(checkLocationPermission())
        instance = this


        if (locationPermissionGranted) {
            setMainContent()
        } else {
            requestLocationPermission()
        }
    }

    private fun setMainContent(isPermitted: Boolean = true){
        setContent {
            val systemUiController = rememberSystemUiController()
            val background = colorResource(id = R.color.background)
            val element = colorResource(id = R.color.element)
            systemUiController.setStatusBarColor(element)

            EstimateAirPressureDecreaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background


                ) {
                    if(isPermitted){
                        MainContent(acc, gps, gra, bar)
                    }else{
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "位置情報の許可がないため", fontSize = 30.sp)
                            Text(text = "アプリを利用できません", fontSize = 30.sp)
                            Spacer(modifier = Modifier.height(30.dp))

                            /*
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(R.color.element),
                                    contentColor = Color.White
                                ),
                                onClick = {
                                    requestLocationPermission()
                                }) {
                                Text("許可を取得", fontSize = 30.sp)
                            }
                             */

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
        gra = Gravity(this)
        bar = Barometric(this)
    }

    //
    override fun onPause() {
        super.onPause()
        acc = Accelerometer(this)
        gps = Gps(this)
        gra = Gravity(this)
        bar = Barometric(this)
    }
}

@Composable
fun MainContent(acc: Accelerometer, gps: Gps, gra: Gravity, bar: Barometric) {
    // Home()
    Sensor(acc, gps, gra, bar, )
    // executionConfirmation()
}

