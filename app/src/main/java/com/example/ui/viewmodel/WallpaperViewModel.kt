package com.example.ui.viewmodel

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.model.Wallpaper
import com.example.data.repository.WallpaperRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallpaperViewModel(private val repository: WallpaperRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val darkThemeSetting = MutableStateFlow("System") // "System", "Light", "Dark"

    // Combine filters reactively
    val wallpapers: StateFlow<List<Wallpaper>> = combine(
        repository.allWallpapers,
        searchQuery,
        selectedCategory
    ) { list, query, cat ->
        list.filter { wp ->
            val matchCategory = cat == "All" || wp.category.equals(cat, ignoreCase = true)
            val matchQuery = query.isEmpty() || 
                    wp.title.contains(query, ignoreCase = true) || 
                    wp.author.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteWallpapers: StateFlow<List<Wallpaper>> = repository.favoriteWallpapers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(wallpaper.id, wallpaper.isFavorite)
        }
    }

    // Set Device Background
    fun setWallpaper(
        context: Context, 
        wallpaper: Wallpaper, 
        option: String, 
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wallpaperManager = WallpaperManager.getInstance(context)
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(wallpaper.url)
                    .build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = (result.drawable as BitmapDrawable).bitmap
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val flag = when (option) {
                            "Home Screen" -> WallpaperManager.FLAG_SYSTEM
                            "Lock Screen" -> WallpaperManager.FLAG_LOCK
                            else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        }
                        if (option == "Both") {
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                        } else {
                            wallpaperManager.setBitmap(bitmap, null, true, flag)
                        }
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                    repository.incrementDownloadCount(wallpaper.id)
                    withContext(Dispatchers.Main) {
                        onComplete(true)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    // Download high-resolution wallpaper safely via MediaStore
    fun downloadWallpaper(
        context: Context, 
        wallpaper: Wallpaper, 
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(wallpaper.url)
                    .build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = (result.drawable as BitmapDrawable).bitmap
                    val filename = "WP_${wallpaper.title.replace(" ", "_")}_${System.currentTimeMillis()}.jpg"
                    
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/AestheticWallpapers")
                            put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    }

                    val resolver = context.contentResolver
                    val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri).use { outputStream ->
                            if (outputStream != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(uri, contentValues, null, null)
                        }
                        repository.incrementDownloadCount(wallpaper.id)
                        withContext(Dispatchers.Main) {
                            onComplete(true)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onComplete(false)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    // Social Sharing function
    fun shareWallpaper(context: Context, wallpaper: Wallpaper) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Download ${wallpaper.title}")
            putExtra(
                Intent.EXTRA_TEXT, 
                "Check out this gorgeous high-res wallpaper: '${wallpaper.title}' by ${wallpaper.author}!\n\nView and Download here: ${wallpaper.url}\n\nShared via Aesthetic Wallpapers."
            )
        }
        context.startActivity(Intent.createChooser(intent, "Share Wallpaper Link"))
    }

    // Live daily specials to simulate the "push notifications for new daily uploads"
    private val dailySpecialsPool = listOf(
        Wallpaper(
            "daily_1", 
            "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?q=80&w=1080&auto=format&fit=crop", 
            "Emerald Fern Veil", 
            "Nature", 
            "Sarah Dorweiler",
            downloadCount = 14
        ),
        Wallpaper(
            "daily_2", 
            "https://images.unsplash.com/photo-1518156677180-95a2893f3e9f?q=80&w=1080&auto=format&fit=crop", 
            "Luminescent Horizon", 
            "Minimalist", 
            "Olia Gozha",
            downloadCount = 8
        ),
        Wallpaper(
            "daily_3", 
            "https://images.unsplash.com/photo-1547891654-e66ed7edd96c?q=80&w=1080&auto=format&fit=crop", 
            "Technicolor Abyss Glow", 
            "Abstract", 
            "Pawel Czerwinski",
            downloadCount = 22
        ),
        Wallpaper(
            "daily_4", 
            "https://images.unsplash.com/photo-1511818966892-d7d671e672a2?q=80&w=1080&auto=format&fit=crop", 
            "Monstera Play of Shadows", 
            "Minimalist", 
            "Jarek Ceborski",
            downloadCount = 5
        ),
        Wallpaper(
            "daily_5", 
            "https://images.unsplash.com/photo-1511447333015-45b65e60f6d5?q=80&w=1080&auto=format&fit=crop", 
            "Interstellar Synth Helix", 
            "Digital Art", 
            "Zoltan Tasi",
            downloadCount = 45
        )
    )

    private var currentDailyIndex = 0

    // Simulate Daily Uploads
    fun simulateDailyUpload(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val nextWallpaper = dailySpecialsPool[currentDailyIndex % dailySpecialsPool.size].copy(
                id = "daily_special_${System.currentTimeMillis()}",
                uploadDate = System.currentTimeMillis()
            )
            repository.insertWallpapers(listOf(nextWallpaper))
            currentDailyIndex++

            withContext(Dispatchers.Main) {
                NotificationHelper.showDailyUploadNotification(
                    context = context,
                    wallpaperTitle = nextWallpaper.title,
                    wallpaperArtist = nextWallpaper.author
                )
            }
        }
    }
}

class WallpaperViewModelFactory(private val repository: WallpaperRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WallpaperViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WallpaperViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
