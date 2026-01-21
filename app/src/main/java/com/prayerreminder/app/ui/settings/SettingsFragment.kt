package com.prayerreminder.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.prayerreminder.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isDarkTheme = false

    private val viewModel: SettingsViewModel by viewModels()

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
        observeViewModel()
    }

    private fun setupSettings() {
        // Theme toggle
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDarkTheme = isChecked
            // In a real app, this would apply the theme
            binding.themeValue.text = if (isChecked) "Dark" else "Light"
            viewModel.onThemeChanged(if (isChecked) "dark" else "light")
        }

        // Calculation method (dummy)
        binding.calculationMethodValue.text = "Muslim World League"
        binding.calculationMethodContainer.setOnClickListener {
            // For now toggle between two dummy methods
            val current = viewModel.settings.value.calculationMethod
            val newMethod = if (current == 4) 5 else 4
            viewModel.onCalculationMethodChanged(newMethod)
            binding.calculationMethodValue.text =
                if (newMethod == 4) "Muslim World League" else "Other Method"
        }

        // Location permission status (dummy)
        binding.locationPermissionValue.text = "Granted"
        binding.locationPermissionValue.setTextColor(
            resources.getColor(android.R.color.holo_green_dark, null)
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.settings.collect { settings ->
                binding.calculationMethodValue.text =
                    if (settings.calculationMethod == 4) "Muslim World League" else "Other Method"
                binding.themeValue.text = when (settings.theme) {
                    "dark" -> "Dark"
                    "light" -> "Light"
                    else -> "System"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
