package com.example.estimateairpressuredecrease.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class Gps(private val context: Context) :LocationListener {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var listener: LocationListener? = null

    private var startTime: Double = 0.0

    @SuppressLint("MissingPermission")
    fun startListening(listener: LocationListener) {
        this.listener = listener
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
    }

    fun stopListening() {
        listener = null
        startTime = 0.0
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lon = location.longitude
        var t = (location.time.toDouble() / 1000) - startTime
        if (startTime == 0.0){
            startTime = t
            t = 0.0
        }

        listener?.onLocationInfoChanged(lat, lon, t)

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
        fun onLocationInfoChanged(lat: Double, lon: Double, t: Double)
    }
}
