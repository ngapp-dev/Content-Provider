package com.ngapp.contentprovider.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.contentprovider.custom_content_provider.Course
import com.ngapp.contentprovider.data.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseListViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = CourseRepository(application)
    private val courseMutableStateFlow = MutableStateFlow<List<Course>?>(null)

    val courseFlow: Flow<List<Course>?>
        get() = courseMutableStateFlow.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            runCatching {
                repository.getCourseList()
            }.onSuccess {
                courseMutableStateFlow.value = it
            }.onFailure { t ->
                courseMutableStateFlow.value = null
                Log.e("CourseListViewModel", "Course load list error", t)
            }
        }
    }

}