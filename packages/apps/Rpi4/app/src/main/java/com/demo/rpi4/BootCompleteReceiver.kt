package com.demo.rpi4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log

private const val TAG = "Rpi4: BootCompleteReceiver"

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: ")
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            if (!userManager.isSystemUser) {
                Log.d(TAG, "onReceive: is not user manager")
                val rpi4ServiceIntent = Intent(context, Rpi4Service::class.java)
                context.startService(rpi4ServiceIntent)
            }
        }
    }
}