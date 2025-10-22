package com.example.spycheck.ui.main.demos.exif.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlin.random.Random

data class PhotoData(val uri: Uri)

class PhotoReader(private val context: Context) {

    fun getPhotoWithLocation(): PhotoData? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Camera")
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val photoUris = mutableListOf<Uri>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count = 0
            while (cursor.moveToNext() && count < 20) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                photoUris.add(contentUri)
                count++
            }
        }

        if (photoUris.isEmpty()) {
            return getRandomPhoto()
        }

        return PhotoData(photoUris[Random.nextInt(photoUris.size)])
    }

    private fun getRandomPhoto(): PhotoData? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
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
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count = 0
            while (cursor.moveToNext() && count < 20) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                photoUris.add(contentUri)
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