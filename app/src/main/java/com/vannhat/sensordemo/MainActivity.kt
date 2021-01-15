// Refer: https://developer.android.com/guide/topics/sensors/sensors_position
package com.vannhat.sensordemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private lateinit var btnGetUpdate: Button
    private lateinit var tvResult: TextView
    private lateinit var directionPoint: DirectionPoint
    private var resultText = ""
    private var sensorResult: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGetUpdate = findViewById(R.id.btnGetOrient)
        tvResult = findViewById(R.id.tvData)
        directionPoint = findViewById(R.id.dpCompass)

        btnGetUpdate.setOnClickListener {
            updateOrientationAngles()

            // First element is Degree in radiant refer to North way
            // Second element is vertical tilt of device refer to ground
            // Third element is horizontal tilt of device refer to ground
            resultText +=
                "\n${sensorResult?.first() ?: 0 * 180 / Math.PI}, " +
                        "${sensorResult?.get(1)}, " +
                        "${sensorResult?.get(2)}"
            tvResult.text = resultText
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
        directionPoint.degree = sensorResult?.firstOrNull() ?: 0f
        directionPoint.invalidate()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "rotationMatrix" now has up-to-date information.
        sensorResult = SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

}
