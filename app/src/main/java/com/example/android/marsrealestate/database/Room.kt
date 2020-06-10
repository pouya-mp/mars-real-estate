package com.example.android.marsrealestate.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.marsrealestate.network.MarsApiFilter

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg properties: DatabaseProperty)

    @Query("SELECT * FROM DatabaseProperty")
    fun getProperties(): LiveData<List<DatabaseProperty>>

}

@Database(entities = [DatabaseProperty::class], version = 1)
abstract class PropertiesDatabase : RoomDatabase() {
    abstract val databaseDao: DatabaseDao
}

private lateinit var INSTANCE: PropertiesDatabase

fun getDatabase(context: Context): PropertiesDatabase {

    synchronized(!::INSTANCE.isInitialized) {
        INSTANCE =
            Room.databaseBuilder(
                context.applicationContext,
                PropertiesDatabase::class.java,
                "properties"
            )
                .build()
    }
    return INSTANCE
}