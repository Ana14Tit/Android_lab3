package com.example.lab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.databinding.WeatherItemBinding

class WeatherAdapter(private var weatherList: List<ForecastItem>,
                     private val cityName: String,
                     private val onForecastItemClick: (ForecastItem, String) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>(){

    class WeatherViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(forecastItem: ForecastItem, cityName: String, onForecastItemClick: (ForecastItem, String) -> Unit) {
            binding.textViewDate.text = forecastItem.date
            binding.textViewTemperature.text = forecastItem.temperature

            itemView.setOnClickListener {
                onForecastItemClick(forecastItem, cityName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.bind(weatherItem, cityName, onForecastItemClick)
    }

    fun updateData(newWeatherList: List<ForecastItem>) {
        weatherList = newWeatherList
        notifyDataSetChanged()
    }
}