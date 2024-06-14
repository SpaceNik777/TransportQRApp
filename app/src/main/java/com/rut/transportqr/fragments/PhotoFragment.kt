package com.rut.transportqr.fragments

import com.rut.transportqr.viewModel.ComplaintViewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rut.transportqr.R
import com.rut.transportqr.databinding.FragmentPhotoBinding
import com.rut.transportqr.model.ComplaintModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoFragment : Fragment() {
    private lateinit var viewBinding: FragmentPhotoBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var currentPhotoFile: File? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private val viewModel: ComplaintViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentPhotoBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        //Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        //Set up the listener for buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.switchCameraButton.setOnClickListener { switchCamera() }
        viewBinding.flashButton.setOnClickListener { flashMode() }
    }

    private fun flashMode() {
        flashMode = when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_OFF
            else -> ImageCapture.FLASH_MODE_OFF
        }
        imageCapture?.flashMode = flashMode
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            //Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            //Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)    //Set the initial flash mode
                .build()
            //Select camera based on lens facing
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            try {
                //Unbind use cases before rebinding
                cameraProvider.unbindAll()
                //Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    private fun takePhoto() {
        val photoFile = createPhotoFile()
        currentPhotoFile = photoFile
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                viewLifecycleOwner.lifecycleScope.launch {
                    //Получаем текущий объект Complaint из ViewModel
                    val complaint = viewModel.complaintModel.value
                    val controller = findNavController()
                    if (complaint != null) {
                        //Обновляем путь к файлу в объекте Complaint
                        complaint.setPhotoFilePath(photoFile.absolutePath)
                        viewModel.setComplaint(complaint)
                        controller.navigate(R.id.previewFragment)
                    } else {
                        val newComplaint = ComplaintModel(
                            "00000",
                            null,
                            null,
                            null,
                            photoFile.absolutePath,
                            null,
                            null
                        )
                        viewModel.setComplaint(newComplaint)
                        controller.navigate(R.id.previewFragment)
                        Log.e(TAG, "Complaint null")
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }
        }
        imageCapture?.takePicture(outputFileOptions, cameraExecutor, imageSavedCallback)
    }

    private fun createPhotoFile(): File {
        val timestamp = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss", Locale.getDefault()).format(Date())
        val photoFileName = "ComplaintPhoto_$timestamp.jpg"
        val storageDir = requireContext().cacheDir
        val file = File(storageDir, photoFileName)
        //Создаем новый файл, если он не существует
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.entries.filter { it.value == false }.map { it.key }
        if (deniedPermissions.isNotEmpty()) {
            //Handle denied permissions
        } else {
            startCamera()
        }
    }

    private fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    companion object {
        private const val TAG = "PhotoFragment"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
