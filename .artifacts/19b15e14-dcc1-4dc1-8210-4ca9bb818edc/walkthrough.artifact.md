# Walkthrough - Course Management Mobile App (MockAPI)

An Android application built with **Jetpack Compose**, **Kotlin**, **Retrofit**, **Coil**, and **Media3 ExoPlayer** to manage courses and categories using MockAPI.io as the backend, fully satisfying the requirements of the course assignment (`Bai_tap_Mobile_Quan_ly_khoa_hoc_MockAPI.pdf`).

## 📱 Features & Screens Implemented

1. **Course List Screen (`CourseListScreen.kt`)**
   - Displays all courses fetched from MockAPI with thumbnails, titles, instructors, prices (formatted in VND), and category badges.
   - Real-time search bar by title, instructor, or description.
   - Horizontal category filter chips to filter courses by category.
   - Floating Action Button (FAB) to add a new course.
   - Top action buttons for Category Management, API Base URL Settings, and Refresh.

2. **Course Detail Screen (`CourseDetailScreen.kt`)**
   - Displays full course details, category name, duration, price, instructor, description, and creation date.
   - Embedded video player using **Media3 ExoPlayer** to play the trailer video directly within the app.
   - Edit and Delete action buttons in the top app bar.

3. **Add / Edit Course Form (`CourseFormScreen.kt`)**
   - Input fields for title, description, instructor, price, duration, thumbnail URL, and trailer URL.
   - Category selection dropdown (`ExposedDropdownMenuBox`) populated dynamically from API categories.
   - Live image preview of the thumbnail URL using Coil.
   - Form validation ensuring required fields, price `>= 0`, duration `>= 0`, and valid category selection.

4. **Category Management Screen (`CategoryManagementScreen.kt`)**
   - Displays the list of categories with course counts.
   - Add and Edit category dialogs.
   - Delete category with validation check (prevents deleting a category that has associated courses).

5. **API Settings Screen (`SettingsScreen.kt`)**
   - Allows configuring and updating the MockAPI Base URL dynamically at runtime.

---

## 🛠️ Technical Stack
- **UI Toolkit:** Jetpack Compose & Material 3
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & `StateFlow`
- **Networking:** Retrofit 2 + Gson Converter + OkHttp Logging Interceptor
- **Image Loading:** Coil Compose
- **Video Playback:** Jetpack Media3 ExoPlayer

---

## 🚀 Verification Results
- **Build Status:** Build finished successfully (`app:assembleDebug`) with zero compilation errors or warnings.
- **API Integration:** Fully asynchronous REST API calls with error handling, loading states, and state management.
