package com.demo.rpi4

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial

private const val TAG = "Rpi4: LightFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [LightFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LightFragment : Fragment() {

    private lateinit var settingsRepository: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        settingsRepository =
            ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)

        val light = view.findViewById<SwitchMaterial>(R.id.swLight)
        val leftIndicator = view.findViewById<SwitchMaterial>(R.id.swLeftIndicator)
        val rightIndicator = view.findViewById<SwitchMaterial>(R.id.swRightIndicator)
        val park = view.findViewById<SwitchMaterial>(R.id.swPark)

        light.setOnCheckedChangeListener(lightStateChangeListener)
        leftIndicator.setOnCheckedChangeListener(leftIndicatorStateChangeListener)
        rightIndicator.setOnCheckedChangeListener(rightIndicatorStateChangeListener)
        park.setOnCheckedChangeListener { _, isSelected ->
            settingsRepository.parkLight(isSelected)
        }

        settingsRepository.headLight.observe(viewLifecycleOwner, Observer {
            light.setOnCheckedChangeListener(null)
            light.isChecked = it
            light.setOnCheckedChangeListener(lightStateChangeListener)
        })

        settingsRepository.leftIndicator.observe(viewLifecycleOwner, Observer {
            leftIndicator.setOnCheckedChangeListener(null)
            leftIndicator.isChecked = it
            leftIndicator.setOnCheckedChangeListener(leftIndicatorStateChangeListener)
        })

        settingsRepository.rightIndicator.observe(viewLifecycleOwner, Observer {
            rightIndicator.setOnCheckedChangeListener(null)
            rightIndicator.isChecked = it
            rightIndicator.setOnCheckedChangeListener(rightIndicatorStateChangeListener)
        })
    }

    private val lightStateChangeListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isSelected ->
            Log.d(TAG, "isSelected: $isSelected")
            settingsRepository.headLight(isSelected)
        }

    private val leftIndicatorStateChangeListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isSelected ->
            settingsRepository.leftIndicator(isSelected)
        }

    private val rightIndicatorStateChangeListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isSelected ->
            settingsRepository.rightIndicator(isSelected)
        }
}