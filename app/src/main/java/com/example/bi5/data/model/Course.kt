package com.example.bi5.data.model

import com.google.gson.annotations.SerializedName

data class Course(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("instructor")
    val instructor: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("trailerUrl")
    val trailerUrl: String,
    @SerializedName("createdAt")
    val createdAt: String = ""
)
