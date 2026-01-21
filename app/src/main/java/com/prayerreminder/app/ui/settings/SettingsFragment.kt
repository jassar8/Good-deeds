package com.prayerreminder.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.prayerreminder.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isDarkTheme = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSettings()
    }

    private fun setupSettings() {
        // Theme toggle
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDarkTheme = isChecked
            // In a real app, this would apply the theme
            binding.themeValue.text = if (isChecked) "Dark" else "Light"
        }

        // Calculation method (dummy)
        binding.calculationMethodValue.text = "Muslim World League"

        // Location permission status (dummy)
        binding.locationPermissionValue.text = "Granted"
        binding.locationPermissionValue.setTextColor(
            resources.getColor(android.R.color.holo_green_dark, null)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
