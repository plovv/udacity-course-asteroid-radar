package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDAO {

    @Query("select * from Asteroid where date(closeApproachDate) >= date('now') order by date(closeApproachDate) asc")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsteroids(asteroids: List<Asteroid>)

    @Query("delete from Asteroid where date(closeApproachDate) < date('now')")
    suspend fun deletePrevious()

    @Query("select * from Asteroid where date(closeApproachDate) = date('now')")
    fun getTodayAsteroids(): LiveData<List<Asteroid>>

}

@Database(entities = [Asteroid::class], version = 1)
abstract class AsteroidDatabase: RoomDatabase() {

    abstract val asteroidDao: AsteroidDAO

}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, AsteroidDatabase::class.java, "asteroids")
                .build()
        }
    }

    return INSTANCE
}