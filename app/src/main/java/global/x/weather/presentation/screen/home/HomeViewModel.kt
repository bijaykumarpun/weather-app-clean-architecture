package global.x.weather.presentation.screen.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import global.x.weather.domain.Outcome
import global.x.weather.domain.models.FavoriteLocationModel
import global.x.weather.domain.models.WeatherData
import global.x.weather.domain.use_cases.device.GetDeviceRegionUseCase
import global.x.weather.domain.use_cases.device.GetSystemCurrentTimeInMillisUseCase
import global.x.weather.domain.use_cases.weather.FetchCurrentWeatherDataUseCase
import global.x.weather.domain.use_cases.weather.FetchHourlyForecastDataUseCase
import global.x.weather.infrastructure.util.toFavoriteLocationModel
import global.x.weather.presentation.screen.WeatherNavigationRoute
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val fetchCurrentWeatherDataUseCase: FetchCurrentWeatherDataUseCase,
    private val fetchHourlyForecastDataUseCase: FetchHourlyForecastDataUseCase,
    private val getSystemCurrentTimeInMillisUseCase: GetSystemCurrentTimeInMillisUseCase,
    private val deviceRegionUseCase: GetDeviceRegionUseCase
) : ViewModel() {
    val currentWeatherData: MutableLiveData<WeatherData.Daily> = MutableLiveData()
    val forecastedWeatherData: MutableLiveData<List<WeatherData.Daily>> = MutableLiveData()

    init {
        val locationModel: String =
            savedStateHandle[WeatherNavigationRoute.Home.argumentName]
                ?: deviceRegionUseCase.invoke()
        onFetchForecastedWeatherData(locationModel)
    }


    fun onFetchCurrentWeatherData() {
        viewModelScope.launch {
            val response = fetchCurrentWeatherDataUseCase.invoke("Pokhara")
            if (response is Outcome.Success) {
                currentWeatherData.value = response.data

            }
        }
    }

    private fun onFetchForecastedWeatherData(locationName: String) {
        viewModelScope.launch {
            val response =
                fetchHourlyForecastDataUseCase.invoke(locationName, 3)
            if (response is Outcome.Success) {
                filterForecastedWeatherResult(response.data)
            }
        }
    }


    private fun filterForecastedWeatherResult(result: List<WeatherData.Daily>) {
        val filteredHourlyData = result[0].hourlyData?.toMutableList()
        filteredHourlyData?.removeIf {
            TimeUnit.SECONDS.toMillis(it.timeEpoch) < getSystemCurrentTimeInMillisUseCase.invoke()
        }
        val newList = mutableListOf<WeatherData.Daily>()
        result.forEachIndexed { index, item ->
            newList.add(
                WeatherData.Daily(
                    location = item.location,
                    country = item.country,
                    localTime = item.localTime,
                    localTimeEpoch = item.localTimeEpoch,
                    updatedAtEpoch = item.updatedAtEpoch,
                    updatedAtTimeString = item.updatedAtTimeString,
                    tempAverage = item.tempAverage,
                    tempMinimum = item.tempMinimum,
                    tempMaximum = item.tempMaximum,
                    isDay = item.isDay,
                    conditionDescription = item.conditionDescription,
                    windSpeed = item.windSpeed,
                    windDegree = item.windDegree,
                    precipitation = item.precipitation,
                    humidity = item.humidity,
                    cloud = item.cloud,
                    hourlyData = if (index == 0) filteredHourlyData else item.hourlyData,
                    region = item.region,
                    date = item.date
                )
            )
        }
        forecastedWeatherData.value = newList

    }

}

