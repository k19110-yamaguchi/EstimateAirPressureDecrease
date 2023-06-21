package com.example.estimateairpressuredecrease.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class Gps(private val context: Context) : Activity(), LocationListener {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var listener: LocationListener? = null

    init {
        // ロケーションの更新を受け取るためにリスナーを登録
    }
    @SuppressLint("MissingPermission")
    fun startListening(listener: LocationListener) {
        this.listener = listener
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            this
        )
    }

    fun stopListening() {
        listener = null
        locationManager.removeUpdates(this)
    }

    fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

   fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        listener?.onLocationChanged(latitude, longitude)

        Log.d("Gps", "Latitude: $latitude, Longitude: $longitude")

        // 位置情報の更新を受け取った後の処理を追加する場合はここに記述してください
    }

    override fun onProviderEnabled(provider: String) {
        // プロバイダが有効になったときの処理を追加する場合はここに記述してください
    }

    override fun onProviderDisabled(provider: String) {
        // プロバイダが無効になったときの処理を追加する場合はここに記述してください
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // ステータスが変更されたときの処理を追加する場合はここに記述してください
    }

    interface LocationListener {
        fun onLocationChanged(lat: Double, lon: Double)
    }
}
