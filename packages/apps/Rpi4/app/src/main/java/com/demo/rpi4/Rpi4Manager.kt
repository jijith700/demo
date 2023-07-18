package com.demo.rpi4

import android.car.Car
import android.content.Context
import android.os.IBinder
import android.os.ServiceManager
import android.util.Log
import vendor.demo.rpi4.IRpi4

/**
 * Constant for logging
 */
private const val TAG = "Rpi4: Rpi4Manager"

/**
 * Constant for Rpi4interface.
 */
private const val IRPI4_AIDL_INTERFACE = "vendor.demo.rpi4.IRpi4/default"

object Rpi4Manager {

    var car: Car? = null

    /**
     * IRpi4 interface.
     */
    var irpi4: IRpi4? = null

    var iRpi4ServiceCallback: IRpi4ServiceCallback? = null

    init {
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

    fun initCar(context: Context) {
        car = Car.createCar(context)
        HardKeyManager.startHardKeys()
        LightManager.init()
    }

    fun onDestroy() {
        HardKeyManager.stopHardKeys()
    }
}