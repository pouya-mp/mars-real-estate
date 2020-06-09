package com.example.android.marsrealestate.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg properties: DatabaseProperties)

    @Query("SELECT * FROM DatabaseProperties")
    fun getProperties(): LiveData<List<DatabaseProperties>>

}

@Database(entities = [DatabaseProperties::class], version = 1)
abstract class PropertiesDatabase : RoomDatabase() {
    abstract val databaseDao: DatabaseDao
}

private lateinit var INSTANCE: PropertiesDatabase

fun getDatabase(context: Context): PropertiesDatabase {

    synchronized(!::INSTANCE.isInitialized) {
        INSTANCE =
                Room.databaseBuilder(context.applicationContext, PropertiesDatabase::class.java, "properties")
                        .build()
    }
    return INSTANCE
}