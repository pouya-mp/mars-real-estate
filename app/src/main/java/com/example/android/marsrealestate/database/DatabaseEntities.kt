package com.example.android.marsrealestate.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseProperties constructor(
        @PrimaryKey
        val id: String,
        val imgSrcUrl: String,
        val type: String,
        val price: Double
)