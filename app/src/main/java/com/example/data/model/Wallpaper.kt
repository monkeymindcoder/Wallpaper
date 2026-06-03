package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "wallpapers")
data class Wallpaper(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val category: String,
    val author: String,
    val isFavorite: Boolean = false,
    val downloadCount: Int = 0,
    val isDailyUpload: Boolean = false,
    val uploadDate: Long = System.currentTimeMillis()
) : Serializable
