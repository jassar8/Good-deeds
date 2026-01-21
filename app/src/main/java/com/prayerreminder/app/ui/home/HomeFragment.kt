package com.prayerreminder.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.prayerreminder.app.databinding.FragmentHomeBinding
import com.prayerreminder.app.ui.home.adapter.PrayerAdapter
import com.prayerreminder.app.ui.home.model.Prayer

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var prayerAdapter: PrayerAdapter

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
        loadPrayerTimes()
    }

    private fun setupRecyclerView() {
        prayerAdapter = PrayerAdapter { prayer, isEnabled ->
            // Handle toggle change
            // In a real app, this would save the preference
        }

        binding.prayerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = prayerAdapter
        }
    }

    private fun loadPrayerTimes() {
        // Dummy prayer times - hardcoded for now
        val prayers = listOf(
            Prayer("Fajr", "05:30", "Dawn prayer", true),
            Prayer("Dhuhr", "12:15", "Midday prayer", true),
            Prayer("Asr", "15:45", "Afternoon prayer", true),
            Prayer("Maghrib", "18:20", "Sunset prayer", true),
            Prayer("Isha", "19:50", "Night prayer", true)
        )

        binding.locationText.text = "New York, USA" // Dummy location
        prayerAdapter.submitList(prayers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
