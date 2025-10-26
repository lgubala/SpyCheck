package com.example.spycheck.ui.main.demos.sneaky.exif

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.exif.utils.ExifDataExtractor
import com.example.spycheck.ui.main.demos.sneaky.exif.utils.PhotoData
import com.example.spycheck.ui.main.demos.sneaky.exif.utils.PhotoExifData
import com.example.spycheck.ui.main.demos.sneaky.exif.utils.PhotoReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExifDemoViewModel : ViewModel() {

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _currentPhoto = MutableStateFlow<PhotoData?>(null)
    val currentPhoto: StateFlow<PhotoData?> = _currentPhoto.asStateFlow()

    private val _exifData = MutableStateFlow<PhotoExifData?>(null)
    val exifData: StateFlow<PhotoExifData?> = _exifData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✅ Add this function to check permission on init
    fun checkPermission(context: Context) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        _hasPermission.value = hasPermission

        // ✅ Auto-load photo if permission granted
        if (hasPermission) {
            loadRandomPhoto(context)
        }
    }

    fun updatePermission(granted: Boolean) {
        _hasPermission.value = granted
    }

    fun loadRandomPhoto(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val photoReader = PhotoReader(context)
                val photo = photoReader.getRandomPhoto()

                if (photo != null) {
                    _currentPhoto.value = photo

                    // Extract EXIF data
                    val extractor = ExifDataExtractor(context)
                    val exif = extractor.extractExifData(photo.uri)
                    _exifData.value = exif
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}