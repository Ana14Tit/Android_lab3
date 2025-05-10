package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lab3.databinding.FragmentDailyWeatherBinding

class DailyWeatherFragment : Fragment() {
    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!
    private var forecastItem: ForecastItem? = null
    private var location: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forecastItem = arguments?.getParcelable("forecastItem")
        location = arguments?.getString("location")

        location?.let {
            binding.textViewDetailCity.text = "City: " + it
        }

        forecastItem?.let {
            binding.textViewDetailDate.text = "Date: " + it.date
            binding.textViewDetailTemperature.text = "Temperature: " + it.temperature
            binding.textViewDetailDescription.text = "Condition: " + it.condition
        }

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, WeatherListFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}