package com.example.lab3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab3.databinding.FragmentWeatherListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import java.net.HttpURLConnection

class WeatherListFragment : Fragment() {
    private var _binding: FragmentWeatherListBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherAdapter: WeatherAdapter
    private var job: Job? = null
    private var availableCities: List<String> = emptyList()
    private var forecasts: List<WeatherData> = emptyList()
    private var selectedCity: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         binding.recyclerViewWeather.apply {
             layoutManager = LinearLayoutManager(requireContext())
         }

         loadWeatherData()
         scheduleWeatherDataUpdates()
    }

    private fun initializeCitySpinner() {
        if (availableCities.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                availableCities
            )
            binding.citySpinner.setAdapter(adapter)
            binding.citySpinner.setOnItemClickListener { _, _, position, _ ->
                val selectedCity = availableCities[position]
                onCitySelected(selectedCity)
            }
        } else {
            showError("No cities available")
        }
    }

    private fun loadWeatherData() {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = WeatherClient.api.getWeatherData()
                withContext(Dispatchers.Main) {
                    if (_binding != null) {
                        if (response.isSuccessful) {
                            val forecastResponse = response.body()
                            forecastResponse?.let {
                                forecasts = it.forecasts
                                availableCities = it.forecasts.map { it.location }
                                initializeCitySpinner()
                                if (availableCities.isNotEmpty()) {
                                    if (availableCities.contains("Voronezh")) {
                                        onCitySelected("Voronezh")
                                    } else {
                                        onCitySelected(availableCities[0])
                                    }
                                }
                            }
                        } else {
                            val errorMessage = "Failed to load weather data: ${response.message()}"
                            showError(errorMessage)
                            Log.e("WeatherListFragment", errorMessage)
                        }
                    } else {
                        val errorMessage = "Binding is null"
                        showError(errorMessage)
                        Log.w("WeatherListFragment", errorMessage)
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    if (_binding != null) {
                        if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                            val errorMessage = "Weather data not found (404)"
                            showError(errorMessage)
                            Log.e("WeatherListFragment", errorMessage)
                        } else {
                            val errorMessage = "HTTP error: ${e.message()}"
                            showError(errorMessage)
                            Log.e("WeatherListFragment", errorMessage)
                        }
                    } else {
                        val errorMessage = "Binding is null"
                        showError(errorMessage)
                        Log.w("WeatherListFragment", errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (_binding != null) {
                        val errorMessage = "An unexpected error occurred: ${e.message}"
                        showError(errorMessage)
                        Log.e("WeatherListFragment", errorMessage)
                    } else {
                        val errorMessage = "Binding is null"
                        showError(errorMessage)
                        Log.w("WeatherListFragment", errorMessage)
                    }
                }
            }
        }
    }

    private fun scheduleWeatherDataUpdates() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                Log.d("WeatherDataUpdates", "Updating weather data...")
                loadWeatherData()
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(60))
            }
        }
        handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(60))
    }

    private fun updateUI(weatherData: WeatherData?) {
        if (weatherData != null) {
            weatherAdapter.updateData(weatherData.forecast)
        } else {
            weatherAdapter.updateData(emptyList())
        }
    }

    private fun onCitySelected(city: String) {
        selectedCity = city
        val selectedForecast = forecasts.find { it.location == city }

        if (selectedForecast != null) {
            weatherAdapter = WeatherAdapter(emptyList(), selectedCity ?: "", onForecastItemClick = { forecastItem, cityName ->
                val bundle = Bundle().apply {
                    putString("location", selectedCity)
                    putParcelable("forecastItem", forecastItem)
                }
                val dailyWeatherFragment = DailyWeatherFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(com.example.lab3.R.id.fragmentContainer, dailyWeatherFragment)
                    .addToBackStack(null)
                    .commit()
            })
            binding.recyclerViewWeather.adapter = weatherAdapter
            updateUI(selectedForecast)
            binding.citySpinner.setText(city, false)
        } else {
            showError("Forecast not found for $city")
            updateUI(null)
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}