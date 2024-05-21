package com.rut.transportqr.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.rut.transportqr.imageCompressor.ImageCompressor
import com.rut.transportqr.model.ComplaintModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Repository : RepositoryInterface {
    private val fireStore = Firebase.firestore
    private val complaintsCollection = fireStore.collection("Обращения")
    private val storageRef = Firebase.storage.reference
    private val imageCompressor = ImageCompressor()
    override fun sendComplaint(complaintModel: ComplaintModel): Task<Void> {
        return getComplaintsCount().continueWithTask { task ->
            if (!task.isSuccessful) {
                return@continueWithTask task.exception?.let { Tasks.forException(it) }
            }
            complaintsCollection.document(createDocumentName(task.result)).set(
                hashMapOf(
                    "Заголовок" to complaintModel.getHeader(),
                    "Сообщение" to complaintModel.getText(),
                    "Код транспорта" to complaintModel.getTransportCode(),
                    "Класс фото происшествия" to complaintModel.getImageClass(),
                    "Вероятность определённого класса фото" to complaintModel.getProbability(),
                    "Ссылка_на_фото" to complaintModel.getPhotoUri(),
                    "Email отправителя" to complaintModel.getSenderEmail()
                )
            )
        }
    }

    override fun uploadImage(
        imageFile: File,
        complaintModel: ComplaintModel
    ): Task<UploadTask.TaskSnapshot> {
        val compressedImageFile = imageCompressor.compressImage(imageFile, 50)
        val timestampPackage = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        val timestamp = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss", Locale.getDefault()).format(Date())
        val uploadTask =
            storageRef.child("hotlinePhotos/$timestampPackage/${complaintModel.getTransportCode()}/${complaintModel.getImageClass()}/${timestamp + "_" + System.currentTimeMillis()}")
                .putBytes(compressedImageFile.readBytes())
        return uploadTask
    }

    override fun getComplaintsCount(): Task<Int> {
        return complaintsCollection.get().continueWith { task ->
            if (!task.isSuccessful) {
                return@continueWith 0
            }
            task.result?.size() ?: 0
        }
    }

    private fun createDocumentName(docsCount: Int): String {
        val timestamp = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss", Locale.getDefault()).format(Date())
        val complaintNumber = docsCount + 1
        return "Обращение № $complaintNumber от $timestamp"
    }

    private fun createComplaintModel(
        transportCode: String? = "00000",
        header: String? = null,
        text: String? = null,
        imageProblemClass: String? = null,
        photoFilePath: String? = null,
        senderEmail: String? = null,
        probability: String? = null,
        photoUrl: String? = null
    ): ComplaintModel {
        return ComplaintModel(
            transportCode,
            header,
            text,
            imageProblemClass,
            photoFilePath,
            senderEmail,
            probability,
            photoUrl
        )
    }

    override fun resetComplaint(): ComplaintModel {
        return createComplaintModel()
    }
}
