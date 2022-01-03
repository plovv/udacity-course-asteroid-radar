package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.Repository
import retrofit2.HttpException
import java.io.IOException

class RefreshAsteroidsDataWorker(val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    companion object {
        const val NAME = "RefreshAsteroidsData"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(context)
        val repo = Repository(database)

        return try {
            repo.refreshAsteroids()
            repo.deletePastAsteroids()

            Result.success()
        } catch (networkException: HttpException) {
            Result.retry()
        } catch (networkException: IOException) {
            Result.retry()
        }
    }

}