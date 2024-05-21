package com.rut.transportqr.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.rut.transportqr.model.ComplaintModel
import java.io.File

interface RepositoryInterface {
    fun sendComplaint(complaintModel: ComplaintModel): Task<Void>
    fun getComplaintsCount(): Task<Int>
    fun uploadImage(imageFile: File, complaintModel: ComplaintModel): Task<UploadTask.TaskSnapshot>
    fun resetComplaint(): ComplaintModel
}
