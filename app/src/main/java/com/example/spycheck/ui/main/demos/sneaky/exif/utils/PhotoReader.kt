package com.example.spycheck.ui.main.demos.sneaky.exif.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import kotlin.random.Random

data class PhotoData(val uri: Uri)

class PhotoReader(private val context: Context) {

    fun getRandomPhoto(): PhotoData? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val photoUris = mutableListOf<Uri>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            var count = 0

            while (cursor.moveToNext() && count < 100) {
                val filePath = cursor.getString(dataColumn)

                // Create file:// URI instead of content:// URI
                if (filePath != null && File(filePath).exists()) {
                    photoUris.add(File(filePath).toUri())
                }
                count++
            }
        }

        return if (photoUris.isNotEmpty()) {
            PhotoData(photoUris[Random.nextInt(photoUris.size)])
        } else {
            null
        }
    }
}