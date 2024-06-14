package com.rut.transportqr.tensorflowClassificator

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.rut.transportqr.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.InputStreamReader

class ImageClassificator {
    private fun getMaxProbabilityIndex(arr: FloatArray): Int {
        var maxVal: Float = 0.0f
        var maxValIndex: Int = -1
        var itemsCountMaxProbabilities: Int = 0
        for (i in arr.indices) {
            if (arr[i] > maxVal) {
                maxValIndex = i
                maxVal = arr[i]
            }
            if (arr[i] >= 100) {
                itemsCountMaxProbabilities++
            }
        }
        return maxValIndex
    }

    fun processImageWithTensorFlow(bitmap: Bitmap, context: Context): List<String> {
        val labels_list = loadLabels(context)
        val resized_bitmap_img: Bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val model = Model.newInstance(context)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
        val byteBuffer = TensorImage.fromBitmap(resized_bitmap_img).buffer
        inputFeature0.loadBuffer(byteBuffer)
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val maxProbabilityIndex: Int = getMaxProbabilityIndex(outputFeature0.floatArray)
        val probability = outputFeature0.floatArray[maxProbabilityIndex] / 255 * 100
        val roundedProbability = "%.2f".format(probability)
        Log.d("Classification",
            "Detected: " + labels_list[maxProbabilityIndex] + ", probability: " + roundedProbability + "%")
        val results = listOf(labels_list[maxProbabilityIndex], roundedProbability)
        model.close()
        return results
    }

    private fun loadLabels(context: Context): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = context.assets.open("labels.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            labels.add(line!!)
        }
        reader.close()
        return labels
    }
}
