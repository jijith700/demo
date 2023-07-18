package com.demo.rpi4

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData


private const val TAG = "Rpi4: SettingsRepository"

class SettingsRepository(context: Context?) {

    private var isBound = false
    var iRpi4ServiceInterface: IRpi4ServiceInterface? = null
    val headLight = MutableLiveData<Boolean>()
    val leftIndicator = MutableLiveData<Boolean>()
    val rightIndicator = MutableLiveData<Boolean>()

    private val connection: ServiceConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.i(TAG, "Service connected")
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            isBound = true
            iRpi4ServiceInterface = IRpi4ServiceInterface.Stub.asInterface(service)
            iRpi4ServiceInterface?.register(iRpi4ServiceCallback)
            headLight.postValue(iRpi4ServiceInterface?.headLightStatus)
            leftIndicator.postValue(iRpi4ServiceInterface?.leftIndicatorStatus)
            rightIndicator.postValue(iRpi4ServiceInterface?.rightIndicatorStatus)
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            isBound = false
            iRpi4ServiceInterface = null
        }
    }

    init {
        val intent = Intent()
        intent.component = ComponentName("com.demo.rpi4", "com.demo.rpi4.Rpi4Service")
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private val iRpi4ServiceCallback: IRpi4ServiceCallback = object : IRpi4ServiceCallback.Stub() {
        override fun onChangeHeadLight(status: Boolean) {
            Log.i(TAG, "onChangeHeadLight: status=$status")
            headLight.postValue(status)
        }

        override fun onChangeLeftIndicator(status: Boolean) {
            Log.i(TAG, "onChangeLeftIndicator: status=$status")
            leftIndicator.postValue(status)
        }

        override fun onChangeRightIndicator(status: Boolean) {
            Log.i(TAG, "onChangeRightIndicator: status=$status")
            rightIndicator.postValue(status)
        }
    }
}