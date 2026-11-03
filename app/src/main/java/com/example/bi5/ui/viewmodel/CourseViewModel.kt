package com.example.bi5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bi5.data.api.RetrofitClient
import com.example.bi5.data.model.Category
import com.example.bi5.data.model.Course
import com.example.bi5.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseViewModel : ViewModel() {
    private val repository = CourseRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _baseUrl = MutableStateFlow(RetrofitClient.baseUrl)
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    val filteredCourses: StateFlow<List<Course>> = combine(_courses, _searchQuery, _selectedCategoryId) { courses, query, catId ->
        courses.filter { course ->
            val matchesQuery = course.title.contains(query, ignoreCase = true) ||
                    course.instructor.contains(query, ignoreCase = true) ||
                    course.description.contains(query, ignoreCase = true)
            val matchesCategory = catId.isNullOrEmpty() || course.categoryId == catId
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val catResult = repository.getCategories()
            val courseResult = repository.getCourses()

            var hasCategoryError = false
            catResult.onSuccess {
                _categories.value = it
                // If categories is empty, try seeding default ones
                if (it.isEmpty()) {
                    val defaultCategories = listOf(
                        Category(name = "Lập trình Mobile", description = "Android, Flutter, React Native"),
                        Category(name = "Lập trình Web", description = "Frontend và Backend"),
                        Category(name = "Trí tuệ nhân tạo", description = "AI, Machine Learning, Deep Learning")
                    )
                    for (cat in defaultCategories) {
                        repository.addCategory(cat)
                    }
                    repository.getCategories().onSuccess { reloaded ->
                        _categories.value = reloaded
                    }
                }
            }.onFailure {
                hasCategoryError = true
            }

            var hasCourseError = false
            courseResult.onSuccess {
                _courses.value = it
            }.onFailure {
                hasCourseError = true
            }

            if (hasCategoryError || hasCourseError) {
                _errorMessage.value = "Lỗi HTTP 404: Vui lòng vào trang MockAPI.io tạo 2 resource 'categories' và 'courses'."
            }

            _isLoading.value = false
        }
    }

    fun updateBaseUrl(newUrl: String) {
        RetrofitClient.baseUrl = newUrl
        _baseUrl.value = RetrofitClient.baseUrl
        loadData()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(catId: String?) {
        _selectedCategoryId.value = catId
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // --- Course CRUD ---
    fun addCourse(course: Course, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addCourse(course).onSuccess {
                _successMessage.value = "Thêm khóa học thành công!"
                loadData()
                onComplete(true)
            }.onFailure {
                _errorMessage.value = "Thêm thất bại: ${it.localizedMessage}"
                onComplete(false)
            }
            _isLoading.value = false
        }
    }

    fun updateCourse(id: String, course: Course, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateCourse(id, course).onSuccess {
                _successMessage.value = "Cập nhật khóa học thành công!"
                loadData()
                onComplete(true)
            }.onFailure {
                _errorMessage.value = "Cập nhật thất bại: ${it.localizedMessage}"
                onComplete(false)
            }
            _isLoading.value = false
        }
    }

    fun deleteCourse(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteCourse(id).onSuccess {
                _successMessage.value = "Xóa khóa học thành công!"
                loadData()
            }.onFailure {
                _errorMessage.value = "Xóa thất bại: ${it.localizedMessage}"
            }
            _isLoading.value = false
        }
    }

    // --- Category CRUD ---
    fun addCategory(category: Category, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addCategory(category).onSuccess {
                _successMessage.value = "Thêm danh mục thành công!"
                loadData()
                onComplete(true)
            }.onFailure {
                _errorMessage.value = "Thêm danh mục thất bại: ${it.localizedMessage}"
                onComplete(false)
            }
            _isLoading.value = false
        }
    }

    fun updateCategory(id: String, category: Category, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value> true
            repository.updateCategory(id, category).onSuccess {
                _successMessage.value = "Cập nhật danh mục thành công!"
                loadData()
                onComplete(true)
            }.onFailure {
                _errorMessage.value = "Cập nhật danh mục thất bại: ${it.localizedMessage}"
                onComplete(false)
            }
            _isLoading.value = false
        }
    }

    fun deleteCategory(id: String, onComplete: (Boolean, String?) -> Unit) {
        val hasCourses = _courses.value.any { it.categoryId == id }
        if (hasCourses) {
            onComplete(false, "Không thể xóa danh mục đang có khóa học liên kết!")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteCategory(id).onSuccess {
                _successMessage.value = "Xóa danh mục thành công!"
                loadData()
                onComplete(true, null)
            }.onFailure {
                _errorMessage.value = "Xóa danh mục thất bại: ${it.localizedMessage}"
                onComplete(false, it.localizedMessage)
            }
            _isLoading.value = false
        }
    }
}
