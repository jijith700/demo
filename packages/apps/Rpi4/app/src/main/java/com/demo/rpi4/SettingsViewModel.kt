package com.demo.rpi4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository: SettingsRepository
    val headLight: MutableLiveData<Boolean>
    val leftIndicator: MutableLiveData<Boolean>
    val rightIndicator: MutableLiveData<Boolean>

    init {
        settingsRepository = SettingsRepository(application.applicationContext)
        headLight = settingsRepository.headLight
        leftIndicator = settingsRepository.leftIndicator
        rightIndicator = settingsRepository.rightIndicator
    }

    fun headLight(isTurnedOn: Boolean) {
        settingsRepository.iRpi4ServiceInterface?.headLight(isTurnedOn)
    }

    fun leftIndicator(isStarted: Boolean) {
        settingsRepository.iRpi4ServiceInterface?.leftIndicator(isStarted)
    }

    fun rightIndicator(isStarted: Boolean) {
        settingsRepository.iRpi4ServiceInterface?.rightIndicator(isStarted)
    }

    fun parkLight(isStarted: Boolean) {
        settingsRepository.iRpi4ServiceInterface?.parkLight(isStarted)
    }
}