package com.example.estimateairpressuredecrease.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// SensorEventListener: リスナーの登録
class Gravity(private val context: Context) : SensorEventListener {
    // 加速度のインスタンスの生成
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    //
    private var gravitySensor: Sensor? = null
    //
    private var listener: GravityListener? = null

    private var startTime: Double = 0.0

    init {
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    // 加速度取得を開始
    fun startListening(listener: GravityListener) {
        this.listener = listener
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
    }

    // 加速度取得を
    fun stopListening() {
        listener = null
        startTime = 0.0
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            var t = (event.timestamp.toDouble() / 1_000_000_000.0) - startTime
            if (startTime == 0.0){
                startTime = t
                t = 0.0
            }
            listener?.onGravityChanged(x, y, z, t)

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something with the sensor accuracy change
    }

    interface GravityListener {
        fun onGravityChanged(x: Double, y: Double, z: Double, t: Double)
    }

}
