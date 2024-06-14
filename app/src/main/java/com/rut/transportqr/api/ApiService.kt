package com.rut.transportqr.api

import com.rut.transportqr.model.PostComplaintModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("create-complaint")
    suspend fun pushComplaint(@Body postComplaintModel: PostComplaintModel): Response<PostComplaintModel>}
