package com.rut.transportqr.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rut.transportqr.imageCompressor.ImageCompressor
import com.rut.transportqr.model.ComplaintModel
import com.rut.transportqr.model.PostComplaintModel
import com.rut.transportqr.repository.RepositoryInterface
import com.rut.transportqr.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ComplaintViewModel : ViewModel() {
    private val _complaintModel = MutableLiveData<ComplaintModel>()
    val complaintModel: LiveData<ComplaintModel> = _complaintModel
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val repository: RepositoryInterface
    val myResponse: MutableLiveData<Response<PostComplaintModel>> = MutableLiveData()

    init {
        repository = Repository()
    }

    fun setComplaint(complaintModel: ComplaintModel) {
        _complaintModel.value = complaintModel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendComplaint() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _isLoading.value = true
                }
                complaintModel.value?.let { complaint ->
                    val imageCompressor = ImageCompressor()
                    val currentTimeInMoscow = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                    val compressedImage =
                        imageCompressor.compressImage(File(complaint.getPhotoFilePath()), 50)
                            .readBytes()
                    val response = repository.sendComplaint(PostComplaintModel(
                        complaint.getTransportCode(),
                        complaint.getHeader(),
                        complaint.getText(),
                        complaint.getImageClass(),
                        complaint.getSenderEmail(),
                        complaint.getProbability(),
                        compressedImage,
                        currentTimeInMoscow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        currentTimeInMoscow.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    ))
                    withContext(Dispatchers.Main) {
                        myResponse.value = response
                        resetComplaint()
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = e.message
                }
            }
        }
    }
    fun resetComplaint() {
        val complaintModel = repository.resetComplaint()
        setComplaint(complaintModel)
    }
}