package com.demo.rpi4

import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.hardware.automotive.vehicle.VehicleProperty
import android.util.Log
import vendor.demo.rpi4.IRpi4
import vendor.demo.rpi4.Pin
import vendor.demo.rpi4.PinMode
import vendor.demo.rpi4.PinStatus
import java.util.concurrent.locks.ReentrantLock


private const val TAG = "Rpi4: LightManager"

/**
 * Constant for period.
 */
private const val PERIOD = 100

/**
 * Constant for thread sleep time.
 */
private const val SLEEP_TIME = 200

/**
 * Constant for indicator thread sleep time.
 */
private const val INDICATOR_SLEEP_TIME = 1000

private const val RATE = 0.0f

object LightManager : CarPropertyManager.CarPropertyEventCallback {

    /**
     * Lock for set gpio operations.
     */
    private val setGpioLock = ReentrantLock()

    /**
     * Thread to handle the left indicator.
     */
    private var leftIndicatorThread: Thread? = null

    /**
     * Thread to handle the right indicator.
     */
    private var rightIndicatorThread: Thread? = null

    /**
     * Thread to handle the pwm.
     */
    private var pwmThread: Thread? = null

    /**
     * IRpi4 interface.
     */
    private var irpi4: IRpi4? = null

    /**
     * Variable to access the CarPropertyManager.
     */
    private var carPropertyManager: CarPropertyManager

    /**
     * Variable to handle the left indicator thread.
     */
    private var startLeftIndicator = true

    /**
     * Variable to handle the right indicator thread.
     */
    private var startRightIndicator = true

    /**
     * Variable to handle the pwm thread.
     */
    private var startpwm = true

    init {
        irpi4 = Rpi4Manager.irpi4
        carPropertyManager =
            Rpi4Manager.car?.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
        irpi4?.configPin(Pin.GPIO_TWENTY_THREE, PinMode.OUTPUT) //Head light
        irpi4?.configPin(Pin.GPIO_TWENTY_FOUR, PinMode.OUTPUT) //Left indicator
        irpi4?.configPin(Pin.GPIO_TWENTY_FIVE, PinMode.OUTPUT) //Right indicator
        //irpi4!!.configPin(Pin.PWM_ZERO, PinMode.PWM_OUTPUT) //PWM
        carPropertyManager.registerCallback(this, VehicleProperty.HEAD_LIGHT, RATE)
        carPropertyManager.registerCallback(this, VehicleProperty.LEFT_INDICATOR, RATE)
        carPropertyManager.registerCallback(this, VehicleProperty.RIGHT_INDICATOR, RATE)
    }

    fun init() {
        Log.i(TAG, "init: ")
    }

    fun headLight(isTurnedOn: Boolean) {
        Log.i(TAG, "headLight: isTurnedOn=$isTurnedOn")
        carPropertyManager.setBooleanProperty(
            VehicleProperty.HEAD_LIGHT,
            carPropertyManager.getAreaId(VehicleProperty.HEAD_LIGHT, 0),
            isTurnedOn
        )
    }

    fun leftIndicator(isStarted: Boolean) {
        Log.i(TAG, "leftIndicator: isTurnedOn=$isStarted")
        carPropertyManager.setBooleanProperty(
            VehicleProperty.LEFT_INDICATOR,
            carPropertyManager.getAreaId(VehicleProperty.LEFT_INDICATOR, 0),
            isStarted
        )
    }

    fun rightIndicator(isStarted: Boolean) {
        Log.i(TAG, "rightIndicator: isTurnedOn=$isStarted")
        carPropertyManager.setBooleanProperty(
            VehicleProperty.RIGHT_INDICATOR,
            carPropertyManager.getAreaId(VehicleProperty.RIGHT_INDICATOR, 0),
            isStarted
        )
    }

    fun parkLight(isStarted: Boolean) {
        Log.d(TAG, "parkLight: isStarted=$isStarted")
        if (isStarted) {
            stopLeftIndicators()
            stopRightIndicators()
            startLeftIndicator()
            startRightIndicator()
        } else {
            stopLeftIndicators()
            stopRightIndicators()
        }
    }

    fun getHeadLightStatus(): Boolean {
        return irpi4!!.getGpio(Pin.GPIO_TWENTY_THREE) == PinStatus.HIGH
    }

    fun getLeftIndicatorStatus(): Boolean {
        return startLeftIndicator
    }

    fun getRightIndicatorStatus(): Boolean {
        return startRightIndicator
    }

    override fun onChangeEvent(value: CarPropertyValue<*>?) {
        Log.i(TAG, "onChangeEvent: propertyId= ${value?.propertyId}, value= ${value?.value}")
        when (value?.propertyId) {
            VehicleProperty.HEAD_LIGHT -> {
                Log.i(TAG, "onChangeEvent: headlight value= ${value.value as Boolean}")
                turnHeadLight(value.value as Boolean)
                Rpi4Manager.iRpi4ServiceCallback?.onChangeHeadLight(value.value as Boolean)
            }

            VehicleProperty.LEFT_INDICATOR -> {
                Log.i(TAG, "onChangeEvent: left indicator value= ${value.value as Boolean}")
                if (value.value as Boolean) {
                    rightIndicator(false)
                    startLeftIndicator()
                    Rpi4Manager.iRpi4ServiceCallback?.onChangeLeftIndicator(true)
                } else {
                    stopLeftIndicators()
                }
            }

            VehicleProperty.RIGHT_INDICATOR -> {
                Log.i(TAG, "onChangeEvent: right indicator value= ${value.value as Boolean}")
                if (value.value as Boolean) {
                    leftIndicator(false)
                    startRightIndicator()
                    Rpi4Manager.iRpi4ServiceCallback?.onChangeRightIndicator(true)
                } else {
                    stopRightIndicators()
                }
            }
        }
    }

    override fun onErrorEvent(propId: Int, zone: Int) {
        Log.e(TAG, "onErrorEvent: propId= $propId zone= $zone")
    }

    private fun turnHeadLight(isTurnedOn: Boolean) {
        Thread {
            Log.d(TAG, "turnHeadLight: isTurnedOn=$isTurnedOn")
            setGpioLock.lock()
            irpi4!!.setGpio(
                Pin.GPIO_TWENTY_THREE,
                if (isTurnedOn) PinStatus.HIGH else PinStatus.LOW
            )
            setGpioLock.unlock()
        }.start()
    }

    /**
     * Method to start the left indicator thread.
     */
    private fun startLeftIndicator() {
        startLeftIndicator = true
        leftIndicatorThread = Thread {
            try {
                Log.d(TAG, "startLeftIndicator: started...")
                while (startLeftIndicator) {
                    setGpioLock.lock()
                    irpi4!!.setGpio(Pin.GPIO_TWENTY_FOUR, PinStatus.HIGH)
                    setGpioLock.unlock()
                    Thread.sleep(INDICATOR_SLEEP_TIME.toLong())
                    setGpioLock.lock()
                    irpi4!!.setGpio(Pin.GPIO_TWENTY_FOUR, PinStatus.LOW)
                    setGpioLock.unlock()
                    Thread.sleep(INDICATOR_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startLeftIndicator: ${e.message}")
            }
        }
        leftIndicatorThread?.start()
    }

    /**
     * Method to start the right indicator thread.
     */
    private fun startRightIndicator() {
        startRightIndicator = true
        rightIndicatorThread = Thread {
            try {
                Log.d(TAG, "startRightIndicator: started...")
                while (startRightIndicator) {
                    setGpioLock.lock()
                    irpi4!!.setGpio(Pin.GPIO_TWENTY_FIVE, PinStatus.HIGH)
                    setGpioLock.unlock()
                    Thread.sleep(INDICATOR_SLEEP_TIME.toLong())
                    setGpioLock.lock()
                    irpi4!!.setGpio(Pin.GPIO_TWENTY_FIVE, PinStatus.LOW)
                    setGpioLock.unlock()
                    Thread.sleep(INDICATOR_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startRightIndicator: ${e.message}")
            }
        }
        rightIndicatorThread?.start()
    }

    private fun stopLeftIndicators() {
        Log.d(TAG, "stopLeftIndicators: ")
        startLeftIndicator = false
        try {
            leftIndicatorThread?.interrupt()
            leftIndicatorThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopLeftIndicators: ${e.message}")
        }
        Thread {
            setGpioLock.lock()
            irpi4!!.setGpio(Pin.GPIO_TWENTY_FOUR, PinStatus.LOW)
            Rpi4Manager.iRpi4ServiceCallback?.onChangeLeftIndicator(false)
            setGpioLock.unlock()
        }.start()
    }

    private fun stopRightIndicators() {
        Log.d(TAG, "stopRightIndicators: ")
        startRightIndicator = false
        try {
            rightIndicatorThread?.interrupt()
            rightIndicatorThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopRightIndicators: ${e.message}")
        }
        Thread {
            setGpioLock.lock()
            irpi4!!.setGpio(Pin.GPIO_TWENTY_FIVE, PinStatus.LOW)
            Rpi4Manager.iRpi4ServiceCallback?.onChangeRightIndicator(false)
            setGpioLock.unlock()
        }.start()
    }

    fun startPwm(cycle: Int) {
        startpwm = true
        pwmThread = Thread {
            try {
                irpi4!!.setPwm(Pin.PWM_ZERO, PERIOD, cycle)
                Log.d(TAG, "startPwm: started...")
                while (startpwm) {
                    for (i in 1..cycle) {
                        irpi4!!.changeDutyCycle(Pin.PWM_ZERO, i)
                        Thread.sleep(SLEEP_TIME.toLong())
                    }

                    for (i in cycle downTo 1) {
                        irpi4!!.changeDutyCycle(Pin.PWM_ZERO, i)
                        Thread.sleep(SLEEP_TIME.toLong())
                    }
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startPwm: ${e.message}")
            }
        }
        pwmThread?.start()
    }
}