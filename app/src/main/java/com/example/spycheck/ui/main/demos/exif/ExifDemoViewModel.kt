package com.example.spycheck.ui.main.demos.exif

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.exif.utils.ExifDataExtractor
import com.example.spycheck.ui.main.demos.exif.utils.PhotoExifData
import com.example.spycheck.ui.main.demos.exif.utils.PhotoReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExifDemoState(
    val hasPermission: Boolean = false,
    val photoData: PhotoExifData? = null,
    val isLoading: Boolean = false
)

class ExifDemoViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(ExifDemoState())
    val state: StateFlow<ExifDemoState> = _state.asStateFlow()

    private val photoReader = PhotoReader(application)
    private val exifExtractor = ExifDataExtractor(application)

    init {
        checkPermission()
    }

    private fun checkPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            getApplication(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        _state.update { it.copy(hasPermission = hasPermission) }
        if (hasPermission) loadPhoto()
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasPermission = granted) }
        if (granted) loadPhoto()
    }

    private fun loadPhoto() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val photo = photoReader.getPhotoWithLocation()
            if (photo != null) {
                val exifData = exifExtractor.extractExifData(photo.uri)
                _state.update { it.copy(photoData = exifData, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun recheckPermission() = checkPermission()
}
