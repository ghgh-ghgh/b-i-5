package com.example.bi5.data.repository

import com.example.bi5.data.api.RetrofitClient
import com.example.bi5.data.model.Category
import com.example.bi5.data.model.Course

class CourseRepository {
    private val api get() = RetrofitClient.apiService

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            Result.success(api.getCategories())
        } catch (e: Exception) {
            try {
                Result.success(api.getCategorySingular())
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addCategory(category: Category): Result<Category> {
        return try {
            Result.success(api.addCategory(category))
        } catch (e: Exception) {
            try {
                Result.success(api.addCategorySingular(category))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateCategory(id: String, category: Category): Result<Category> {
        return try {
            Result.success(api.updateCategory(id, category))
        } catch (e: Exception) {
            try {
                Result.success(api.updateCategorySingular(id, category))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteCategory(id: String): Result<Category> {
        return try {
            Result.success(api.deleteCategory(id))
        } catch (e: Exception) {
            try {
                Result.success(api.deleteCategorySingular(id))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCourses(): Result<List<Course>> {
        return try {
            Result.success(api.getCourses())
        } catch (e: Exception) {
            try {
                Result.success(api.getCoursesSingular())
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCourseById(id: String): Result<Course> {
        return try {
            Result.success(api.getCourseById(id))
        } catch (e: Exception) {
            try {
                Result.success(api.getCourseByIdSingular(id))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addCourse(course: Course): Result<Course> {
        return try {
            Result.success(api.addCourse(course))
        } catch (e: Exception) {
            try {
                Result.success(api.addCourseSingular(course))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateCourse(id: String, course: Course): Result<Course> {
        return try {
            Result.success(api.updateCourse(id, course))
        } catch (e: Exception) {
            try {
                Result.success(api.updateCourseSingular(id, course))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteCourse(id: String): Result<Course> {
        return try {
            Result.success(api.deleteCourse(id))
        } catch (e: Exception) {
            try {
                Result.success(api.deleteCourseSingular(id))
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }
}
