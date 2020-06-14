package com.example.android.marsrealestate.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.marsrealestate.database.DatabaseProperty
import com.example.android.marsrealestate.database.PropertiesDatabase
import com.example.android.marsrealestate.database.asDomainModel
import com.example.android.marsrealestate.domain.Property
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PropertiesRepository(private val database: PropertiesDatabase) {

    private var currentFilter: MarsApiFilter = MarsApiFilter.ALL_PROPERTIES

    val properties: LiveData<List<Property>> =
            Transformations.map(database.databaseDao.getProperties()) {
                it.filter { property ->
                    return@filter if (currentFilter == MarsApiFilter.ALL_PROPERTIES) {
                        true
                    } else {
                        property.type == currentFilter.propertyType
                    }
                }.asDomainModel()
            }

    suspend fun refreshProperties(filter: MarsApiFilter) {
        currentFilter = filter
        withContext(Dispatchers.IO) {
            val marsProperties = MarsApi.retrofitService.getProperties(currentFilter.propertyType)
            database.databaseDao.insertAll(*marsProperties.asDatabaseModel())
        }
    }


    fun getproperty(id: String): DatabaseProperty {
        return database.databaseDao.getProperty(id)
    }

}