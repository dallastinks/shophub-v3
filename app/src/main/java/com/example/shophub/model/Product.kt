package com.example.shophub.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: String,
    val categoryId: Int,
    val description: String,
    val isFavorite: Boolean = false
)
