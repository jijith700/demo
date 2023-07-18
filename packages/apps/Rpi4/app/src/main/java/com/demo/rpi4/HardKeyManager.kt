package com.demo.rpi4

import android.car.Car
import android.car.CarOccupantZoneManager
import android.car.hardware.property.CarPropertyManager
import android.car.input.CarInputManager
import android.hardware.automotive.vehicle.VehicleProperty
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import vendor.demo.rpi4.IRpi4
import vendor.demo.rpi4.Pin
import vendor.demo.rpi4.PinMode
import vendor.demo.rpi4.PinStatus
import java.util.concurrent.locks.ReentrantLock

private const val TAG = "Rpi4: HardKeyManager"

/**
 * Constant for hard key thread sleep time.
 */
private const val HARD_KEY_SLEEP_TIME = 150

object HardKeyManager {

    /**
     * Lock for get gpio operations.
     */
    private val getGpioLock = ReentrantLock()

    /**
     * To access the car input manager.
     */
    private var carInputManager: CarInputManager

    /**
     * IRpi4 interface.
     */
    private var irpi4: IRpi4? = null

    /**
     * Variable to access the CarPropertyManager.
     */
    private var carPropertyManager: CarPropertyManager

    /**
     * Thread to handle the home key.
     */
    private var homeKeyThread: Thread? = null

    /**
     * Thread to handle the back key.
     */
    private var backKeyThread: Thread? = null

    /**
     * Thread to handle the play key.
     */
    private var playKeyThread: Thread? = null

    /**
     * Thread to handle the pause key.
     */
    private var pauseKeyThread: Thread? = null

    /**
     * Thread to handle the next key.
     */
    private var nextKeyThread: Thread? = null

    /**
     * Thread to handle the previous key.
     */
    private var previousKeyThread: Thread? = null

    /**
     * Thread to handle the head light key.
     */
    private var headLightKeyThread: Thread? = null

    /**
     * Thread to handle the left indicator key.
     */
    private var leftIndicatorKeyThread: Thread? = null

    /**
     * Thread to handle the right indicator key.
     */
    private var rightIndicatorKeyThread: Thread? = null

    /**
     * Variable to handle the hard key thread
     */
    private var startHardKey = true

    /**
     * To handle the home key action.
     */
    private var homeButtonPressed = false

    /**
     * To handle the back key action.
     */
    private var backButtonPressed = false

    /**
     * To handle the play key action.
     */
    private var playButtonPressed = false

    /**
     * To handle the pause key action.
     */
    private var pauseButtonPressed = false

    /**
     * To handle the next key action.
     */
    private var nextButtonPressed = false

    /**
     * To handle the previous key action.
     */
    private var previousButtonPressed = false

    /**
     * To handle the head light key action.
     */
    private var headLightButtonPressed = false

    /**
     * To handle the left indicator key action.
     */
    private var leftIndicatorButtonPressed = false

    /**
     * To handle the right indicator key action.
     */
    private var rightIndicatorButtonPressed = false

    init {
        irpi4 = Rpi4Manager.irpi4
        carInputManager = Rpi4Manager.car?.getCarManager(Car.CAR_INPUT_SERVICE) as CarInputManager
        carPropertyManager =
            Rpi4Manager.car?.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
        //Hard keys
        irpi4?.configPin(Pin.GPIO_SEVENTEEN, PinMode.INPUT) //Home key
        irpi4?.configPin(Pin.GPIO_TWENTY_SEVEN, PinMode.INPUT) //Back key
        irpi4?.configPin(Pin.GPIO_TWENTY_TWO, PinMode.INPUT) //Play
        irpi4?.configPin(Pin.GPIO_FIVE, PinMode.INPUT) //Pause
        irpi4?.configPin(Pin.GPIO_SIX, PinMode.INPUT) //Next
        irpi4?.configPin(Pin.GPIO_TWENTY_SIX, PinMode.INPUT) //Previous
        //Lig? switches
        irpi4?.configPin(Pin.GPIO_ZERO, PinMode.INPUT) //Head light
        irpi4?.configPin(Pin.GPIO_ONE, PinMode.INPUT) //Left indicator
        irpi4?.configPin(Pin.GPIO_SIXTEEN, PinMode.INPUT) //Right indicator

        startHardKeys()
    }

    fun startHardKeys() {
        Log.i(TAG, "startHardKeys: ")
        startHardKey = true
        try {
            startHomeKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startHomeKeyListener ${e.message}")
        }
        try {
            startBackKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startBackKeyListener ${e.message}")
        }
        try {
            startPlayKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startPlayKeyListener ${e.message}")
        }
        try {
            startPauseKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startPlayPauseKeyListener ${e.message}")
        }
        try {
            startNextKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startNextKeyListener ${e.message}")
        }
        try {
            startPreviousKeyListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startPreviousKeyListener ${e.message}")
        }
        //Light switches
        try {
            startHeadLightListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startHeadLightListener ${e.message}")
        }
        try {
            startLefIndicatorListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startLefIndicatorListener ${e.message}")
        }
        try {
            startRightIndicatorListener()
        } catch (e: java.lang.RuntimeException) {
            Log.e(TAG, "startHardKeys: startRightIndicatorListener ${e.message}")
        }
    }

    fun stopHardKeys() {
        Log.i(TAG, "stopHardKeys: ")
        startHardKey = false
        try {
            homeKeyThread?.interrupt()
            homeKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: homeKeyThread ${e.message}")
        }
        try {
            backKeyThread?.interrupt()
            backKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: backKeyThread ${e.message}")
        }
        try {
            playKeyThread?.interrupt()
            playKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: playKeyThread ${e.message}")
        }
        try {
            pauseKeyThread?.interrupt()
            pauseKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: pauseKeyThread ${e.message}")
        }
        try {
            nextKeyThread?.interrupt()
            nextKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: nextKeyThread ${e.message}")
        }
        try {
            previousKeyThread?.interrupt()
            previousKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: previousKeyThread ${e.message}")
        }
        //Light switches
        try {
            headLightKeyThread?.interrupt()
            headLightKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: headLightKeyThread ${e.message}")
        }
        try {
            leftIndicatorKeyThread?.interrupt()
            leftIndicatorKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: leftIndicatorKeyThread ${e.message}")
        }
        try {
            rightIndicatorKeyThread?.interrupt()
            rightIndicatorKeyThread?.join()
        } catch (e: Exception) {
            Log.e(TAG, "stopHardKeys: rightIndicatorKeyThread ${e.message}")
        }
    }

    @Throws(RuntimeException::class)
    private fun startHomeKeyListener() {
        homeKeyThread = Thread {
            try {
                Log.d(TAG, "startHomeKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_SEVENTEEN)
                    if (status == PinStatus.HIGH && !homeButtonPressed) {
                        Log.e(TAG, "startHomeKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        homeButtonPressed = true
                    } else if (status == PinStatus.LOW && homeButtonPressed) {
                        Log.i(TAG, "startHomeKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        homeButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startHomeKeyListener: ${e.message}")
            }
        }
        homeKeyThread?.start()
    }

    @Throws(RuntimeException::class)
    private fun startBackKeyListener() {
        backKeyThread = Thread {
            try {
                Log.d(TAG, "startBackKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_TWENTY_SEVEN)
                    if (status == PinStatus.HIGH && !backButtonPressed) {
                        Log.e(TAG, "startBackKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        backButtonPressed = true
                    } else if (status == PinStatus.LOW && backButtonPressed) {
                        Log.i(TAG, "startBackKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        backButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startBackKeyListener: ${e.message}")
            }
        }
        backKeyThread?.start()
    }

    private fun startPlayKeyListener() {
        playKeyThread = Thread {
            try {
                Log.d(TAG, "startPlayKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_TWENTY_TWO)
                    if (status == PinStatus.HIGH && !playButtonPressed) {
                        Log.e(TAG, "startPlayKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY
                            ), CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        playButtonPressed = true
                    } else if (status == PinStatus.LOW && playButtonPressed) {
                        Log.i(TAG, "startPlayKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(
                                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY
                            ), CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        playButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startPlayKeyListener: ${e.message}")
            }
        }
        playKeyThread?.start()
    }

    private fun startPauseKeyListener() {
        pauseKeyThread = Thread {
            try {
                Log.d(TAG, "startPauseKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_FIVE)
                    if (status == PinStatus.HIGH && !pauseButtonPressed) {
                        Log.e(TAG, "startPauseKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE
                            ), CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        pauseButtonPressed = true
                    } else if (status == PinStatus.LOW && pauseButtonPressed) {
                        Log.i(TAG, "startPauseKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(
                                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE
                            ), CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        pauseButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startPauseKeyListener: ${e.message}")
            }
        }
        pauseKeyThread?.start()
    }


    @Throws(RuntimeException::class)
    private fun startNextKeyListener() {
        nextKeyThread = Thread {
            try {
                Log.d(TAG, "startNextKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_SIX)
                    if (status == PinStatus.HIGH && !nextButtonPressed) {
                        Log.e(TAG, "startNextKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        nextButtonPressed = true
                    } else if (status == PinStatus.LOW && nextButtonPressed) {
                        Log.i(TAG, "startNextKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        nextButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startNextKeyListener: ${e.message}")
            }
        }
        nextKeyThread?.start()
    }

    @Throws(RuntimeException::class)
    private fun startPreviousKeyListener() {
        previousKeyThread = Thread {
            try {
                Log.d(TAG, "startPreviousKeyListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_TWENTY_SIX)
                    if (status == PinStatus.HIGH && !previousButtonPressed) {
                        Log.e(TAG, "startPreviousKeyListener: Button pressed...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        previousButtonPressed = true
                    } else if (status == PinStatus.LOW && previousButtonPressed) {
                        Log.i(TAG, "startPreviousKeyListener: Button released...")
                        carInputManager.injectKeyEvent(
                            newKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS),
                            CarOccupantZoneManager.DISPLAY_TYPE_MAIN
                        )
                        previousButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startPreviousKeyListener: ${e.message}")
            }
        }
        previousKeyThread?.start()
    }

    @Throws(RuntimeException::class)
    private fun startHeadLightListener() {
        headLightKeyThread = Thread {
            try {
                Log.d(TAG, "startHeadLightListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_ZERO)
                    if (status == PinStatus.HIGH && !headLightButtonPressed) {
                        Log.e(TAG, "startHeadLightListener: Button pressed...")
                        val headLightStatus =
                            carPropertyManager.getBooleanProperty(VehicleProperty.HEAD_LIGHT, 0)
                        Log.d(TAG, "startHeadLightListener: headLightStatus= $headLightStatus")
                        carPropertyManager.setBooleanProperty(
                            VehicleProperty.HEAD_LIGHT,
                            carPropertyManager.getAreaId(VehicleProperty.HEAD_LIGHT, 0),
                            !headLightStatus
                        )
                        headLightButtonPressed = true
                    } else if (status == PinStatus.LOW && headLightButtonPressed) {
                        Log.i(TAG, "startHeadLightListener: Button released...")
                        headLightButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startHeadLightListener: ${e.message}")
            }
        }
        headLightKeyThread?.start()
    }

    @Throws(RuntimeException::class)
    private fun startLefIndicatorListener() {
        leftIndicatorKeyThread = Thread {
            try {
                Log.d(TAG, "startLefIndicatorListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_ONE)
                    if (status == PinStatus.HIGH && !leftIndicatorButtonPressed) {
                        Log.e(TAG, "startLefIndicatorListener: Button pressed...")
                        val leftIndicatorStatus =
                            carPropertyManager.getBooleanProperty(VehicleProperty.LEFT_INDICATOR, 0)
                        Log.d(
                            TAG,
                            "startLefIndicatorListener: leftIndicatorStatus= $leftIndicatorStatus"
                        )
                        carPropertyManager.setBooleanProperty(
                            VehicleProperty.LEFT_INDICATOR,
                            carPropertyManager.getAreaId(VehicleProperty.LEFT_INDICATOR, 0),
                            !leftIndicatorStatus
                        )
                        leftIndicatorButtonPressed = true
                    } else if (status == PinStatus.LOW && leftIndicatorButtonPressed) {
                        Log.i(TAG, "startLefIndicatorListener: Button released...")
                        leftIndicatorButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startLefIndicatorListener: ${e.message}")
            }
        }
        leftIndicatorKeyThread?.start()
    }

    @Throws(RuntimeException::class)
    private fun startRightIndicatorListener() {
        rightIndicatorKeyThread = Thread {
            try {
                Log.d(TAG, "startRightIndicatorListener: started")
                while (startHardKey) {
                    getGpioLock.lock()
                    val status: Byte = irpi4!!.getGpio(Pin.GPIO_SIXTEEN)
                    if (status == PinStatus.HIGH && !rightIndicatorButtonPressed) {
                        val rightIndicatorStatus = carPropertyManager.getBooleanProperty(
                            VehicleProperty.RIGHT_INDICATOR, 0
                        )
                        Log.d(
                            TAG,
                            "startRightIndicatorListener: rightIndicatorStatus= $rightIndicatorStatus"
                        )
                        carPropertyManager.setBooleanProperty(
                            VehicleProperty.RIGHT_INDICATOR,
                            carPropertyManager.getAreaId(VehicleProperty.RIGHT_INDICATOR, 0),
                            !rightIndicatorStatus
                        )
                        rightIndicatorButtonPressed = true
                    } else if (status == PinStatus.LOW && rightIndicatorButtonPressed) {
                        Log.i(TAG, "startRightIndicatorListener: Button released...")
                        rightIndicatorButtonPressed = false
                    }
                    getGpioLock.unlock()
                    Thread.sleep(HARD_KEY_SLEEP_TIME.toLong())
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "startRightIndicatorListener: ${e.message}")
            }
        }
        rightIndicatorKeyThread?.start()
    }

    private fun newKeyEvent(action: Int, keyCode: Int): KeyEvent {
        val currentTime = SystemClock.uptimeMillis()
        return KeyEvent( /* downTime = */currentTime,  /* eventTime = */
            currentTime,
            action,
            keyCode,  /* repeat = */
            0
        )
    }
}