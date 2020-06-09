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

package com.example.android.marsrealestate.network

import com.example.android.marsrealestate.database.DatabaseProperties
import com.example.android.marsrealestate.domain.Property
import com.squareup.moshi.Json

data class MarsProperty(
        val id: String,
        @Json(name = "img_src") val imgSrcUrl: String,
        val type: String,
        val price: Double
)

data class MarsPropertiesContainer(val properties: List<MarsProperty>)

fun MarsPropertiesContainer.asDomainModel(): List<Property> {
    return properties.map {
        Property(it.id, it.imgSrcUrl, it.type, it.price)
    }
}

fun MarsPropertiesContainer.asDatabaseModel(): List<DatabaseProperties> {
    return properties.map {
        DatabaseProperties(it.id, it.imgSrcUrl, it.type, it.price)
    }
}