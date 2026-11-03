package com.example.bi5.data.api

import com.example.bi5.data.model.Category
import com.example.bi5.data.model.Course
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("category")
    suspend fun getCategorySingular(): List<Category>

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): Category

    @GET("category/{id}")
    suspend fun getCategoryByIdSingular(@Path("id") id: String): Category

    @POST("categories")
    suspend fun addCategory(@Body category: Category): Category

    @POST("category")
    suspend fun addCategorySingular(@Body category: Category): Category

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body category: Category): Category

    @PUT("category/{id}")
    suspend fun updateCategorySingular(@Path("id") id: String, @Body category: Category): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Category

    @DELETE("category/{id}")
    suspend fun deleteCategorySingular(@Path("id") id: String): Category

    @GET("courses")
    suspend fun getCourses(): List<Course>

    @GET("course")
    suspend fun getCoursesSingular(): List<Course>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") id: String): Course

    @GET("course/{id}")
    suspend fun getCourseByIdSingular(@Path("id") id: String): Course

    @POST("courses")
    suspend fun addCourse(@Body course: Course): Course

    @POST("course")
    suspend fun addCourseSingular(@Body course: Course): Course

    @PUT("courses/{id}")
    suspend fun updateCourse(@Path("id") id: String, @Body course: Course): Course

    @PUT("course/{id}")
    suspend fun updateCourseSingular(@Path("id") id: String, @Body course: Course): Course

    @DELETE("courses/{id}")
    suspend fun deleteCourse(@Path("id") id: String): Course

    @DELETE("course/{id}")
    suspend fun deleteCourseSingular(@Path("id") id: String): Course
}
