package com.example.racingcar.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.racingcar.ui.models.MovementInput.Accelerometer
import com.example.racingcar.ui.theme.RacingCarTheme
import com.example.racingcar.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity(), SensorEventListener {
    private val viewModel by viewModels<MainViewModel>()

    private val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val accelerometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCollectors()

        setContent {
            RacingCarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RacingCarGameNavHost()
                }
            }
        }
    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.movementInput.collect {
                    when (it) {
                        Accelerometer -> registerAccelerometer()
                        else -> unregisterAccelerometer()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerAccelerometer()
        viewModel.playBackgroundMusic()
    }

    private fun registerAccelerometer() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI   // SENSOR_DELAY_GAME was too much!
        )
    }

    private fun unregisterAccelerometer() {
        sensorManager.unregisterListener(this)
    }

    override fun onPause() {
        super.onPause()
        unregisterAccelerometer()
        viewModel.stopBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseSounds()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val accelerationX = event.values[0]
        val accelerationY = event.values[1]
        val accelerationZ = event.values[2]

        viewModel.setAcceleration(accelerationX, accelerationY, accelerationZ)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}