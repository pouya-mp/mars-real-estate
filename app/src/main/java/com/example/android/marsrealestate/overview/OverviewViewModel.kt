/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.app.Application
import androidx.lifecycle.*
import com.example.android.marsrealestate.database.getDatabase
import com.example.android.marsrealestate.domain.Property
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.repository.PropertiesRepository
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PropertiesRepository(getDatabase(application))

    private val _status = MutableLiveData<MarsApiStatus>()

    private val _propertiesFilterStatus = MutableLiveData(MarsApiFilter.ALL_PROPERTIES)
    val propertiesFilterStatus: LiveData<MarsApiFilter>
        get() = _propertiesFilterStatus

    val status: LiveData<MarsApiStatus>
        get() = _status

    val properties: LiveData<List<Property>> = repository.properties

    private val _shouldNavigateToPropertyDetails = MutableLiveData<String>()
    val shouldNavigateToPropertyDetails: LiveData<String>
        get() = _shouldNavigateToPropertyDetails


    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.ALL_PROPERTIES)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        val oldValue = propertiesFilterStatus.value
        _propertiesFilterStatus.value = filter
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                repository.refreshProperties(filter)
                _status.value = MarsApiStatus.DONE
            } catch (t: Throwable) {
                _status.value = MarsApiStatus.ERROR
                _propertiesFilterStatus.value = oldValue
            }
        }
    }

    fun refreshProperties(
        filter: MarsApiFilter = _propertiesFilterStatus.value ?: MarsApiFilter.ALL_PROPERTIES
    ) {
        getMarsRealEstateProperties(filter)
    }

    fun onPropertyClicked(propertyId: String) {
        _shouldNavigateToPropertyDetails.value = propertyId
    }

    fun doneNavigateToPropertyDetails() {
        _shouldNavigateToPropertyDetails.value = ""
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

}
