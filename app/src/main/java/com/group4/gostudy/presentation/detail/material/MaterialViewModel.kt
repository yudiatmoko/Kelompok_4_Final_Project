package com.group4.gostudy.presentation.detail.material

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.gostudy.data.repository.CourseRepository
import com.group4.gostudy.model.Chapter
import com.group4.gostudy.model.Course
import com.group4.gostudy.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MaterialViewModel(
    private val courseRepo: CourseRepository
) : ViewModel() {

    private val _course = MutableLiveData<ResultWrapper<Course>>()
    val course: LiveData<ResultWrapper<Course>>
        get() = _course

    private val _chapter = MutableLiveData<ResultWrapper<List<Chapter>>>()
    val chapter: LiveData<ResultWrapper<List<Chapter>>>
        get() = _chapter

    fun getCourseById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            courseRepo.getCourseById(id).collect {
                _course.postValue(it)
            }
        }
    }
    fun getChaptersV2(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            courseRepo.getChaptersV2(id).collect {
                _chapter.postValue(it)
            }
        }
    }

//    private val _modules = MutableLiveData<ResultWrapper<List<Module>>>()
//    val modules: LiveData<ResultWrapper<List<Module>>>
//        get() = _modules
//
//    fun getModule() {
//        viewModelScope.launch(Dispatchers.IO) {
//            chapterRepo.getModules().collect() {
//                _modules.postValue(it)
//            }
//        }
//    }
}
