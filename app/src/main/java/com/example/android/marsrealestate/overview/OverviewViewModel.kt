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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    private val _status = MutableLiveData<MarsApiStatus>()

    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

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
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                val result = MarsApi.retrofitService.getProperties(filter.propertyType)
                _status.value = MarsApiStatus.DONE
                _properties.value = result
            } catch (t: Throwable) {
                _status.value = MarsApiStatus.ERROR
                _properties.value = emptyList()
            }
        }
    }

    private fun applyFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    private var propertiesFilterStatus = MarsApiFilter.ALL_PROPERTIES

    fun onRentClicked() {
        applyFilter(MarsApiFilter.FOR_RENT_PROPERTIES)
        propertiesFilterStatus = MarsApiFilter.FOR_RENT_PROPERTIES
    }

    fun onBuyClicked() {
        applyFilter(MarsApiFilter.FOR_SALE_PROPERTIES)
        propertiesFilterStatus = MarsApiFilter.FOR_SALE_PROPERTIES
    }

    fun onShowAllClicked() {
        applyFilter(MarsApiFilter.ALL_PROPERTIES)
        propertiesFilterStatus = MarsApiFilter.ALL_PROPERTIES
    }

    private val _doneRefreshingProperties = MutableLiveData(false)
    val doneRefreshingProperties: LiveData<Boolean>
        get() = _doneRefreshingProperties


    fun refreshProperties() {
        getMarsRealEstateProperties(propertiesFilterStatus)
        _doneRefreshingProperties.value = true
    }


}
