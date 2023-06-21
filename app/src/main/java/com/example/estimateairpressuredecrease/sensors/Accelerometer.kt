package com.example.estimateairpressuredecrease.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// SensorEventListener: リスナーの登録
class Accelerometer(private val context: Context) : SensorEventListener {
    // 加速度のインスタンスの生成
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    //
    private var accelerometerSensor: Sensor? = null
    //
    // private val accelerationData = mutableListOf<Triple<Double, Double, Double>>()
    private var listener: AccListener? = null

    init {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // 加速度取得を開始
    fun startListening(listener: AccListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // 加速度取得を
    fun stopListening() {
        listener = null
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            // accelerationData.add(Triple(x, y, z))
            listener?.onAccelerationChanged(x, y, z)
            // Log.d("Acceleration", "x: ${x}, y: ${y}, z: ${z},")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something with the sensor accuracy change
    }

    interface AccListener {
        fun onAccelerationChanged(x: Double, y: Double, z: Double)
    }

}
