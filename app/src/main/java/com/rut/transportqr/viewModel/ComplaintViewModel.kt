package com.rut.transportqr.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rut.transportqr.model.ComplaintModel
import com.rut.transportqr.repository.RepositoryInterface
import com.rut.transportqr.repository.Repository
import kotlinx.coroutines.launch
import java.io.File

class ComplaintViewModel : ViewModel() {
    private val _complaintModel = MutableLiveData<ComplaintModel>()
    val complaintModel: LiveData<ComplaintModel> = _complaintModel
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val repository: RepositoryInterface

    init {
        repository = Repository()
    }

    fun setComplaint(complaintModel: ComplaintModel) {
        _complaintModel.value = complaintModel
    }

    fun startSendingComplaint() {
        uploadPhoto()
    }

    private fun uploadPhoto() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val complaint = complaintModel.value
                val imageFile = complaint?.getPhotoFilePath()?.let { File(it) }
                if (imageFile != null) {
                    val uploadTask = repository.uploadImage(imageFile, complaint)
                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                            complaint.setPhotoUri(downloadUrl.toString())
                            setComplaint(complaint)
                            sendComplaint()
                        }
                    }.addOnFailureListener { exception ->
                        _isLoading.value = false
                        _errorMessage.value = exception.message
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "Фото жалобы не может быть пустым!"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
            }
        }
    }

    private fun sendComplaint() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                complaintModel.value?.let { complaint ->
                    repository.sendComplaint(complaint)
                        .addOnSuccessListener {
                            _isLoading.value = false
                            resetComplaint()
                            setIsComplaintSent(true)
                        }
                        .addOnFailureListener { exception ->
                            _isLoading.value = false
                            _errorMessage.value = exception.message
                        }
                } ?: run {
                    _isLoading.value = false
                    _errorMessage.value = "Жалоба не может быть пустой"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
            }
        }
    }

    private fun resetComplaint() {
        val complaintModel = repository.resetComplaint()
        setComplaint(complaintModel)
    }

    private val _isComplaintSent = MutableLiveData<Boolean>(false)
    val isComplaintSent: LiveData<Boolean> = _isComplaintSent
    fun setIsComplaintSent(value: Boolean) {
        _isComplaintSent.postValue(value)
    }
}
