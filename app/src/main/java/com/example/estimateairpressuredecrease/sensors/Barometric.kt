package com.example.estimateairpressuredecrease.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// SensorEventListener: リスナーの登録
class Barometric(private val context: Context) : SensorEventListener {
    // 加速度のインスタンスの生成
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    //
    private var barometricSensor: Sensor? = null
    //
    // private val accelerationData = mutableListOf<Triple<Double, Double, Double>>()
    private var listener: BarListener? = null

    private var startTime: Double = 0.0

    init {
        barometricSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    }

    // 加速度取得を開始
    fun startListening(listener: BarListener) {
        this.listener = listener
        sensorManager.registerListener(this, barometricSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // 加速度取得を
    fun stopListening() {
        listener = null
        startTime = 0.0
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PRESSURE) {
            val bar = event.values[0].toDouble()
            var t = (event.timestamp.toDouble() / 1_000_000_000.0) - startTime
            if (startTime == 0.0){
                startTime = t
                t = 0.0
            }
            listener?.onBarometricChanged(bar, t)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something with the sensor accuracy change
    }

    interface BarListener {
        fun onBarometricChanged(bar: Double, t:Double)
    }

}
