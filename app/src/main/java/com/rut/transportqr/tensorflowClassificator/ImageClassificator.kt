package com.rut.transportqr.tensorflowClassificator

import android.content.Context
import android.graphics.Bitmap
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
            //Find the index of the maximum probability item
            if (arr[i] > maxVal) {
                maxValIndex = i
                maxVal = arr[i]
            }
            //Find the number of items with probability >= 100
            if (arr[i] >= 100) {
                itemsCountMaxProbabilities++
            }
        }
        return maxValIndex
    }

    fun processImageWithTensorFlow(bitmap: Bitmap, context: Context): List<String> {
        //check if image is uploaded by user
        if (bitmap == null) {
            Toast.makeText(context, "no bitmap", Toast.LENGTH_SHORT).show()
        }
        val labels_list = loadLabels(context)
        //resize uploadedimage
        val resized_bitmap_img: Bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        //paste the tflite model code here
        val model = Model.newInstance(context)
        //Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
        //create bytebuffer image from the resized bitmap
        val byteBuffer = TensorImage.fromBitmap(resized_bitmap_img).buffer
        inputFeature0.loadBuffer(byteBuffer)
        //Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        //show output in textView of layout file
        //outputFeature0 array has probabilities of 1000 values
        val maxProbabilityIndex: Int = getMaxProbabilityIndex(outputFeature0.floatArray)
        val probability = outputFeature0.floatArray[maxProbabilityIndex] / 255 * 100
        val roundedProbability = "%.2f".format(probability)
        val resulttxt =
            "Detected: " + labels_list[maxProbabilityIndex] + ", probability: " + roundedProbability + "%"
        val results = listOf(labels_list[maxProbabilityIndex], roundedProbability)
        //Releases model resources if no longer used.
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
