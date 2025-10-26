package com.example.spycheck.ui.main.demos.sneaky.exif.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlin.random.Random

data class PhotoData(val uri: Uri)

class PhotoReader(private val context: Context) {

    /**
     * Get a random photo from ALL photo folders (DCIM, WhatsApp, Messenger, etc.)
     */
    fun getRandomPhoto(): PhotoData? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA  // ✅ Add this to see file paths
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val photoUris = mutableListOf<Uri>()

        // ✅ Query ALL images, not filtered by folder
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,  // ✅ No selection - gets ALL images
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count = 0
            // ✅ Increased limit to get more diverse photos
            while (cursor.moveToNext() && count < 100) {
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