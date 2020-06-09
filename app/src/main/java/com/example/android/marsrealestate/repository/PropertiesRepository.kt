package com.example.android.marsrealestate.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.marsrealestate.database.PropertiesDatabase
import com.example.android.marsrealestate.database.asDomainModel
import com.example.android.marsrealestate.domain.Property
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsPropertiesContainer
import com.example.android.marsrealestate.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PropertiesRepository(private val database: PropertiesDatabase) {

    val properties: LiveData<List<Property>> = Transformations.map(database.databaseDao.getProperties()) {
        it.asDomainModel()
    }

    suspend fun refreshProperties(filter: String) {
        withContext(Dispatchers.IO) {
            val marsProperties = MarsApi.retrofitService.getProperties(filter)
            val container = MarsPropertiesContainer(marsProperties)
            Log.i("here", "property type is ${marsProperties[0].type}")
            database.databaseDao.insertAll(*container.asDatabaseModel())
        }
    }

}