package com.rut.transportqr.imageCompressor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

class ImageCompressor {
    private fun getExifOrientation(file: File): Int {
        val exif = ExifInterface(file.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return exifOrientationToDegrees(orientation)
    }

    private fun exifOrientationToDegrees(orientation: Int): Int {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
    fun compressImage(file: File, quality: Int): File {
        val compressedFile = File(file.parent, "compressed_${file.name}")
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val orientation = getExifOrientation(file)
        val rotatedBitmap = rotateImageIfRequired(bitmap, orientation)

        val outputStream = FileOutputStream(compressedFile)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        return compressedFile
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


}