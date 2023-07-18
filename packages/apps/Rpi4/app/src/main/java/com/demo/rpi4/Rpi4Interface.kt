package com.demo.rpi4

import vendor.demo.rpi4.Pin
import vendor.demo.rpi4.PinStatus

object Rpi4Interface : IRpi4ServiceInterface.Stub() {

    override fun register(iRpi4ServiceCallback: IRpi4ServiceCallback?) {
        Rpi4Manager.iRpi4ServiceCallback = iRpi4ServiceCallback
    }

    override fun unregister() {
        Rpi4Manager.iRpi4ServiceCallback = null
    }

    override fun headLight(isTurnedOn: Boolean) {
        LightManager.headLight(isTurnedOn)
    }

    override fun leftIndicator(isStarted: Boolean) {
        LightManager.leftIndicator(isStarted);
    }

    override fun rightIndicator(isStarted: Boolean) {
        LightManager.rightIndicator(isStarted);
    }

    override fun parkLight(isStarted: Boolean) {
        LightManager.parkLight(isStarted)
    }

    override fun getHeadLightStatus(): Boolean {
        return LightManager.getHeadLightStatus()
    }

    override fun getLeftIndicatorStatus(): Boolean {
        return LightManager.getLeftIndicatorStatus()
    }

    override fun getRightIndicatorStatus(): Boolean {
        return LightManager.getRightIndicatorStatus()
    }
}