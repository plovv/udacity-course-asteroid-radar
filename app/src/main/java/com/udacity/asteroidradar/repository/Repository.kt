package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val database: AsteroidDatabase) {

    val weekAsteroid = database.asteroidDao.getAsteroids()
    val todayAsteroid = database.asteroidDao.getTodayAsteroids()

    suspend fun deletePastAsteroids() {
        database.asteroidDao.deletePrevious()
    }

    suspend fun getPictureOfDay(): PictureOfDay {
        return withContext(Dispatchers.IO) {
           Network.nasaApi.getPictureOfDay()
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            val from = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val to = dateFormat.format(calendar.time)

            val asteroids = Network.nasaApi.getAsteroids(from, to).let { json ->
                val jsonObj = JSONObject(json)

                parseAsteroidsJsonResult(jsonObj)
            }

            database.asteroidDao.insertAsteroids(asteroids)
        }
    }

}