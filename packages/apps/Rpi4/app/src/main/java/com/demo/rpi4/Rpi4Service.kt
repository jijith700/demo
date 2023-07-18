package com.demo.rpi4

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

private const val TAG = "Rpi4: Rpi4Service"

class Rpi4Service : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: Rpi4Service started...")
        Rpi4Manager.initCar(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return Rpi4Interface
    }

    override fun onDestroy() {
        Rpi4Manager.onDestroy()
        super.onDestroy()
    }

}