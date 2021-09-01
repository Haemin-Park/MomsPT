package com.fitsionary.momspt.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutLandmarkDao {
    /**
     * workout
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(vararg workout: DatabaseWorkout)

    /**
     * landmark
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLandmark(vararg landmark: DatabaseLandmark)

    /**
     * workout - landmark
     */
    @Transaction
    @Query("select * from workouts")
    fun getWorkoutWithLandmark(): LiveData<List<DatabaseWorkoutWithLandmark>>

}

@Database(entities = [DatabaseWorkout::class, DatabaseLandmark::class], version = 1)
abstract class WorkoutLandmarkDatabase : RoomDatabase() {
    abstract val dao: WorkoutLandmarkDao
}

private lateinit var INSTANCE: WorkoutLandmarkDatabase

fun getDatabase(context: Context): WorkoutLandmarkDatabase {
    synchronized(WorkoutLandmarkDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                WorkoutLandmarkDatabase::class.java, "WorkoutLandmarkDatabase"
            ).build()
        }
    }
    return INSTANCE
}