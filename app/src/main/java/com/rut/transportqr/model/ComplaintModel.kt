package com.rut.transportqr.model

import android.os.Parcel
import android.os.Parcelable

data class ComplaintModel(
    private var transportCode: String?,
    private var header: String?,
    private var text: String?,
    private var imageProblemClass: String?,
    private var photoFilePath: String?,
    private var senderEmail: String?,
    private var probability: String?,
    private var photoUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transportCode)
        parcel.writeString(header)
        parcel.writeString(text)
        parcel.writeString(imageProblemClass)
        parcel.writeString(photoFilePath)
        parcel.writeString(senderEmail)
        parcel.writeString(probability)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ComplaintModel> {
        override fun createFromParcel(parcel: Parcel): ComplaintModel {
            return ComplaintModel(parcel)
        }

        override fun newArray(size: Int): Array<ComplaintModel?> {
            return arrayOfNulls(size)
        }
    }

    fun getTransportCode(): String? {
        return transportCode
    }

    fun setTransportCode(trCode: String) {
        transportCode = trCode
    }

    fun getProbability(): String? {
        return probability
    }

    fun setProbability(probability_: String) {
        probability = probability_
    }

    fun getText(): String? {
        return text
    }

    fun setText(txt: String) {
        text = txt
    }

    fun getHeader(): String? {
        return header
    }

    fun setHeader(head: String) {
        header = head
    }

    fun getPhotoFilePath(): String? {
        return photoFilePath
    }

    fun setPhotoFilePath(photoFP: String) {
        photoFilePath = photoFP
    }

    fun setSenderEmail(sEmail: String) {
        senderEmail = sEmail
    }

    fun getSenderEmail(): String? {
        return senderEmail
    }

    fun setImageClass(imgClass: String) {
        imageProblemClass = imgClass
    }

    fun getImageClass(): String? {
        return imageProblemClass
    }

    fun setPhotoUri(pUri: String?) {
        photoUrl = pUri
    }

    fun getPhotoUri(): String? {
        return photoUrl
    }
}
