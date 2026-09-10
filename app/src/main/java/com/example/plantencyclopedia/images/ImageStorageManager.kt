package com.example.plantencyclopedia.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageManager {

    fun getImagesDirectory(context: Context): File {
        val dir = File(context.filesDir, "plant_images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    suspend fun saveImageFromUri(context: Context, uri: Uri, maxWidth: Int = 1200): String = withContext(Dispatchers.IO) {
        val imagesDir = getImagesDirectory(context)
        val fileName = "plant_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
        val destinationFile = File(imagesDir, fileName)

        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Could not decode image")

            // Scale down if larger than maxWidth
            val scaledBitmap = if (originalBitmap.width > maxWidth) {
                val ratio = maxWidth.toFloat() / originalBitmap.width.toFloat()
                val targetHeight = (originalBitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(originalBitmap, maxWidth, targetHeight, true)
            } else {
                originalBitmap
            }

            FileOutputStream(destinationFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            originalBitmap.recycle()

            return@withContext destinationFile.absolutePath
        } finally {
            inputStream?.close()
        }
    }

    suspend fun saveBitmap(context: Context, bitmap: Bitmap, maxWidth: Int = 1200): String = withContext(Dispatchers.IO) {
        val imagesDir = getImagesDirectory(context)
        val fileName = "plant_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
        val destinationFile = File(imagesDir, fileName)

        val scaledBitmap = if (bitmap.width > maxWidth) {
            val ratio = maxWidth.toFloat() / bitmap.width.toFloat()
            val targetHeight = (bitmap.height * ratio).toInt()
            Bitmap.createScaledBitmap(bitmap, maxWidth, targetHeight, true)
        } else {
            bitmap
        }

        FileOutputStream(destinationFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
        }

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        return@withContext destinationFile.absolutePath
    }

    suspend fun deleteImageFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                file.delete()
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun getAllLocalImageFiles(context: Context): List<File> {
        val dir = getImagesDirectory(context)
        return dir.listFiles()?.filter { it.isFile } ?: emptyList()
    }
}
