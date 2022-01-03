package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val WEEK_FILTER = "week"
private const val TODAY_FILTER = "today"

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = Repository(getDatabase(app))

    private var currentFiltering: String = WEEK_FILTER

    private val weekAsteroids = repo.weekAsteroid
    private val todayAsteroid = repo.todayAsteroid

    val asteroids = MediatorLiveData<List<Asteroid>>()

    private val _pictureOfDayUrl = MutableLiveData<String>()
    val pictureOfDayUrl: LiveData<String>
        get() =  _pictureOfDayUrl

    private val _networkError = MutableLiveData<Boolean>(false)
    val networkError: LiveData<Boolean>
        get() = _networkError
    fun networkErrorShown() { _networkError.value = false }

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails: LiveData<Asteroid?>
        get() = _navigateToAsteroidDetails

    fun setAsteroidDetailsToNavigate(asteroid: Asteroid) { _navigateToAsteroidDetails.value = asteroid }
    fun navigationToAsteroidDetailsDone() { _navigateToAsteroidDetails.value = null }

    init {
        refreshDataFromRepository()
        initPictureOfDayUrl()

        asteroids.addSource(weekAsteroids) {
            it.let {
                if (currentFiltering == WEEK_FILTER) {
                    asteroids.value = weekAsteroids.value
                }
            }
        }

        asteroids.addSource(todayAsteroid) {
            it.let {
                if (currentFiltering == TODAY_FILTER) {
                    asteroids.value = todayAsteroid.value
                }
            }
        }
    }

    fun showWeekAsteroids() {
        refreshDataFromRepository()

        currentFiltering = WEEK_FILTER
        asteroids.value = weekAsteroids.value
    }

    fun showTodayAsteroids() {
        currentFiltering = TODAY_FILTER
        asteroids.value = todayAsteroid.value
    }

    fun showSavedAsteroids() {
        currentFiltering = WEEK_FILTER
        asteroids.value = weekAsteroids.value
    }

    private fun initPictureOfDayUrl() {
        viewModelScope.launch {
            try {
                val pictureOfDay = repo.getPictureOfDay()

                if(pictureOfDay.mediaType == "image") {
                    _pictureOfDayUrl.value = pictureOfDay.url
                }else {
                    _pictureOfDayUrl.value = ""
                }
            } catch (networkError: HttpException) {
                if(_pictureOfDayUrl.value.isNullOrEmpty() && !_networkError.value!!)
                    _networkError.value = true
            }
        }
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                repo.refreshAsteroids()
            } catch (networkError: HttpException) {
                if(asteroids.value.isNullOrEmpty() && !_networkError.value!!)
                    _networkError.value = true
            }
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }

            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }

}