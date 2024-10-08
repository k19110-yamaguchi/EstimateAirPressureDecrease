package com.example.estimateairpressuredecrease.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

// SensorEventListener: リスナーの登録
class Accelerometer(private val context: Context) : SensorEventListener {
    // 加速度のインスタンスの生成
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    //
    private var accelerometerSensor: Sensor? = null
    //
    private var listener: AccListener? = null

    private var startTime: Double = 0.0

    init {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // 加速度取得を開始
    fun startListening(listener: AccListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    // 加速度取得を停止
    fun stopListening() {
        listener = null
        startTime = 0.0
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            var t = (event.timestamp.toDouble() / 1_000_000_000.0) - startTime
            if (startTime == 0.0){
                startTime = t
                t = 0.0
            }
            listener?.onAccelerationChanged(x, y, z, t)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something with the sensor accuracy change
    }

    interface AccListener {
        fun onAccelerationChanged(x: Double, y: Double, z: Double, t: Double)
    }

}
