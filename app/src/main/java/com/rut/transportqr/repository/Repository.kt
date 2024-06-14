package com.rut.transportqr.repository

import com.rut.transportqr.api.ApiClient
import com.rut.transportqr.imageCompressor.ImageCompressor
import com.rut.transportqr.model.ComplaintModel
import com.rut.transportqr.model.PostComplaintModel
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class Repository : RepositoryInterface {
    override suspend fun sendComplaint(postComplaintModel: PostComplaintModel): Response<PostComplaintModel> {
        return ApiClient.api.pushComplaint(postComplaintModel)
    }

    private fun createComplaintModel(
        transportCode: String? = "00000",
        header: String? = null,
        text: String? = null,
        imageProblemClass: String? = null,
        photoFilePath: String? = null,
        senderEmail: String? = null,
        probability: String? = null,
    ): ComplaintModel {
        return ComplaintModel(
            transportCode,
            header,
            text,
            imageProblemClass,
            photoFilePath,
            senderEmail,
            probability
        )
    }

    override fun resetComplaint(): ComplaintModel {
        return createComplaintModel()
    }
}
