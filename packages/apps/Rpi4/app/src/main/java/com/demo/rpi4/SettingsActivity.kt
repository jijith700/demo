package com.demo.rpi4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.rpi4.decorators.VerticalDividerItemDecoration


private const val TAG = "Rpi4: MainActivity"

/**
 * Activity class to display the RPI4 UI.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    /**
     * Lifecycle on create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initialize()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: ")
        super.onDestroy()
    }

    /**
     * Method to initialize all ui elements.
     */
    private fun initialize() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val rvVehicleSettings = findViewById<RecyclerView>(R.id.rvVehicleSettings)

        val settings = resources.getStringArray(R.array.settings)
        val settingsAdapter = SettingsAdapter(settings)

        rvVehicleSettings.layoutManager = LinearLayoutManager(this)
        val decoration = VerticalDividerItemDecoration(15, false)
        rvVehicleSettings.addItemDecoration(decoration)
        rvVehicleSettings.adapter = settingsAdapter
    }
}

