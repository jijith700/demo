package com.demo.rpi4

import android.os.Bundle
import android.os.IBinder
import android.os.ServiceManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import vendor.demo.rpi4.IRpi4
import vendor.demo.rpi4.Pin
import vendor.demo.rpi4.PinMode

/**
 * Activity class to display the RPI4 UI.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        /**
         * Constant for logging
         */
        const val TAG = "Rpi4: MainActivity"

        /**
         * Constant for Rpi4interface.
         */
        const val IRPI4_AIDL_INTERFACE = "vendor.demo.rpi4.IRpi4/default"

        /**
         * Constant for period.
         */
        const val PERIOD = 100;

        /**
         * Constant for thread sleep time.
         */
        const val SLEEP_TIME = 300;
    }

    /**
     * To show the cycle value error.
     */
    private lateinit var tilError: TextInputLayout

    /**
     * To get the cycle value.
     */
    private lateinit var tieCycle: TextInputEditText

    /**
     * IRpi4 interface.
     */
    private var irpi4: IRpi4? = null

    /**
     * Variable to store the cycle value.
     */
    private var cycle = 0;

    /**
     * Variable to handle the thread.
     */
    private var start = true;

    /**
     * Lifecycle on create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    /**
     * Method to initialize all ui elements.
     */
    private fun initialize() {
        tilError = findViewById<TextInputLayout>(R.id.tilError)
        tieCycle = findViewById<TextInputEditText>(R.id.tiedtCycle)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        btnStart.setOnClickListener(startListener)
        btnStop.setOnClickListener(stopListener)

        val binder: IBinder? = ServiceManager.getService(IRPI4_AIDL_INTERFACE)
        binder.let {
            irpi4 = IRpi4.Stub.asInterface(it)
            if (irpi4 == null) {
                Log.e(TAG, "Getting IRpi4 AIDL daemon interface failed!")
            } else {
                Log.d(TAG, "IRpi4 AIDL daemon interface is bounded!")
            }
        }
    }

    /**
     * Method to start the PWM thread.
     */
    private fun startPwm() {
        Thread {
            irpi4!!.configPin(Pin.PWM_ZERO, PinMode.PWM_OUTPUT)
            irpi4!!.setPwm(Pin.PWM_ZERO, PERIOD, cycle)
            Log.d(TAG, "startPwm: started....")
            while (start) {
                for (i in 1..cycle) {
                    try {
                        irpi4!!.changeDutyCycle(Pin.PWM_ZERO, i)
                        Thread.sleep(SLEEP_TIME.toLong())
                    } catch (e: Exception) {
                        Log.e(TAG, "startPwm: exception 1= ${e.message}")
                    }
                }

                for (i in cycle downTo 1) {
                    try {
                        irpi4!!.changeDutyCycle(Pin.PWM_ZERO, i)
                        Thread.sleep(SLEEP_TIME.toLong())
                    } catch (e: Exception) {
                        Log.e(TAG, "startPwm: exception 2= ${e.message}")
                    }
                }
            }
        }.start()
    }

    /**
     * Click listener for start button.
     */
    private val startListener: View.OnClickListener = View.OnClickListener {
        if (!TextUtils.isEmpty(tieCycle.text)) {
            cycle = Integer.parseInt(tieCycle.text.toString())
            start = true
            startPwm()
        } else {
            tilError.error = "Invalid cycle value"
            start = false;
        }
    }

    /**
     * Click listener for stop button.
     */
    private val stopListener: View.OnClickListener = View.OnClickListener {
        start = false;
    }
}

