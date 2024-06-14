package com.rut.transportqr.repository


import com.rut.transportqr.model.ComplaintModel
import com.rut.transportqr.model.PostComplaintModel
import retrofit2.Response


interface RepositoryInterface {
    suspend fun sendComplaint(postComplaintModel: PostComplaintModel): Response<PostComplaintModel>
    fun resetComplaint(): ComplaintModel
}
