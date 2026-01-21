package com.prayerreminder.app.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.prayerreminder.app.databinding.FragmentHomeBinding
import com.prayerreminder.app.domain.PrayerName
import com.prayerreminder.app.ui.home.adapter.PrayerAdapter
import com.prayerreminder.app.ui.home.model.Prayer

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var prayerAdapter: PrayerAdapter

    private val viewModel: HomeViewModel by viewModels()

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onLocationPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        checkLocationPermissionAndLoad()
    }

    private fun setupRecyclerView() {
        prayerAdapter = PrayerAdapter { prayer ->
            val mappedName = when (prayer.name) {
                "الفجر" -> PrayerName.FAJR
                "الشروق" -> PrayerName.SUNRISE
                "الظهر" -> PrayerName.DHUHR
                "العصر" -> PrayerName.ASR
                "المغرب" -> PrayerName.MAGHRIB
                "العشاء" -> PrayerName.ISHA
                else -> null
            }

            mappedName?.let {
                viewModel.onPrayerReminderToggled(it, !prayer.reminderEnabled)
            } ?: Toast.makeText(
                requireContext(),
                getString(com.prayerreminder.app.R.string.prayer_toast_placeholder),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.prayerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = prayerAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                val city = state.city
                if (city != null) {
                    binding.cityNameTextView?.text = city
                }

                val todayTimes = state.todayPrayerTimes
                if (todayTimes != null) {
                    val prayers = listOf(
                        Prayer("الفجر", todayTimes.times[PrayerName.FAJR]?.toString() ?: "", "", state.reminderSettings.enableFajr),
                        Prayer("الشروق", todayTimes.times[PrayerName.SUNRISE]?.toString() ?: "", "", state.reminderSettings.enableSunrise),
                        Prayer("الظهر", todayTimes.times[PrayerName.DHUHR]?.toString() ?: "", "", state.reminderSettings.enableDhuhr),
                        Prayer("العصر", todayTimes.times[PrayerName.ASR]?.toString() ?: "", "", state.reminderSettings.enableAsr),
                        Prayer("المغرب", todayTimes.times[PrayerName.MAGHRIB]?.toString() ?: "", "", state.reminderSettings.enableMaghrib),
                        Prayer("العشاء", todayTimes.times[PrayerName.ISHA]?.toString() ?: "", "", state.reminderSettings.enableIsha)
                    )
                    prayerAdapter.submitList(prayers)
                }

                val nextInfo = state.nextPrayerInfo
                if (nextInfo != null) {
                    val remaining = nextInfo.remaining
                    val hours = remaining.toHours()
                    val minutes = (remaining.toMinutes() % 60)
                    val seconds = (remaining.seconds % 60)
                    binding.countdownTextView?.text =
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
        }
    }

    private fun checkLocationPermissionAndLoad() {
        val context = context ?: return
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.onLocationPermissionResult(true)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
