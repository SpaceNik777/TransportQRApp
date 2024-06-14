package com.rut.transportqr.model

data class PostComplaintModel(
    val transportCode: String?,
    val header: String?,
    val text: String?,
    val imageProblemClass: String?,
    val senderEmail: String?,
    val probability: String?,
    val photo: ByteArray?,
    val createDate: String?,
    val createTime: String?
)
