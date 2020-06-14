/*
 *  Copyright 2018, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.marsrealestate.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.android.marsrealestate.database.DatabaseProperty
import com.example.android.marsrealestate.database.getDatabase
import com.example.android.marsrealestate.repository.PropertiesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The [ViewModel] that is associated with the [DetailFragment].
 */
class DetailViewModel(app: Application, propertyId: String) : AndroidViewModel(app) {

    private val repository = PropertiesRepository(getDatabase(app))
    private lateinit var marsProperty: DatabaseProperty

    private val _property = MutableLiveData<DatabaseProperty>()
    val property : LiveData<DatabaseProperty>
        get() = _property

    private fun getProperty(propertyId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                marsProperty = repository.getproperty(propertyId)


            }
            _property.value = marsProperty
        }
    }

    init {
        getProperty(propertyId)
    }

}
