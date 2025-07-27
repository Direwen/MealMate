package com.minsikhein_bj01lr.mealmate.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import kotlin.getValue

class ImageStorageHelper(
    context: Context
) {
    private val context by lazy { context.applicationContext }
    private val imageDirectory = "recipe_images"

    fun saveImage(uri: Uri): String? {
        return try {
            // Create directory if it doesn't exist
            val dir = File(context.filesDir, imageDirectory)
            if (!dir.exists()) dir.mkdirs()

            // Create unique filename
            val filename = "${UUID.randomUUID()}.jpg"
            val file = File(dir, filename)

            // Copy the image
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Return relative path
            "$imageDirectory/$filename"
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun loadImage(path: String): Bitmap? {
        return try {
            val file = File(context.filesDir, path)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(path: String): Boolean {
        return try {
            val file = File(context.filesDir, path)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}