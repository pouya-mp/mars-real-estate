package com.example.android.marsrealestate.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.marsrealestate.domain.Property

@Entity
data class DatabaseProperty(
    @PrimaryKey
    val id: String,
    val imgSrcUrl: String,
    val type: String,
    val price: Double
)

fun List<DatabaseProperty>.asDomainModel(): List<Property> {
    return map {
        Property(it.id, it.imgSrcUrl, it.type, it.price)
    }
}