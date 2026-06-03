package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Wallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers ORDER BY uploadDate DESC")
    fun getAllWallpapers(): Flow<List<Wallpaper>>

    @Query("SELECT * FROM wallpapers WHERE category = :category ORDER BY uploadDate DESC")
    fun getWallpapersByCategory(category: String): Flow<List<Wallpaper>>

    @Query("SELECT * FROM wallpapers WHERE isFavorite = 1 ORDER BY uploadDate DESC")
    fun getFavoriteWallpapers(): Flow<List<Wallpaper>>

    @Query("SELECT * FROM wallpapers WHERE id = :id LIMIT 1")
    suspend fun getWallpaperById(id: String): Wallpaper?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpapers(wallpapers: List<Wallpaper>)

    @Update
    suspend fun updateWallpaper(wallpaper: Wallpaper)

    @Query("UPDATE wallpapers SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Query("UPDATE wallpapers SET downloadCount = downloadCount + 1 WHERE id = :id")
    suspend fun incrementDownloadCount(id: String)
}
