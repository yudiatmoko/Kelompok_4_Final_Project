package com.group4.gostudy.data.repository

import com.group4.gostudy.data.network.api.datasource.GoStudyApiDataSource
import com.group4.gostudy.data.network.api.model.category.toCategoryList
import com.group4.gostudy.data.network.api.model.coursev2.toCourse
import com.group4.gostudy.data.network.api.model.coursev2.toCourseList
import com.group4.gostudy.model.Category
import com.group4.gostudy.model.Course
import com.group4.gostudy.utils.ResultWrapper
import com.group4.gostudy.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

/*
Hi, Code Enthusiast!
https://github.com/yudiatmoko
*/

interface CourseRepository {
    suspend fun getCategories(): Flow<ResultWrapper<List<Category>>>

    suspend fun getCourses(
        category: String?,
        search: String?,
        type: String?,
        level: String?,
        promoPrecentage: Boolean?,
        createAt: Boolean?
    ): Flow<ResultWrapper<List<Course>>>

    suspend fun getCourseById(id: Int): Flow<ResultWrapper<Course>>
}

class CourseRepositoryImpl(
    private val apiDataSource: GoStudyApiDataSource
) : CourseRepository {
    override suspend fun getCategories(): Flow<ResultWrapper<List<Category>>> {
        return proceedFlow {
            apiDataSource.getCategories().data?.categories?.toCategoryList() ?: emptyList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(2000)
        }
    }

    override suspend fun getCourses(
        category: String?,
        search: String?,
        type: String?,
        level: String?,
        promoPrecentage: Boolean?,
        createAt: Boolean?
    ): Flow<ResultWrapper<List<Course>>> {
        return proceedFlow {
            apiDataSource.getCourses(
                category,
                search,
                type,
                level,
                promoPrecentage,
                createAt
            ).data?.courses?.toCourseList() ?: emptyList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(2000)
        }
    }

    override suspend fun getCourseById(id: Int): Flow<ResultWrapper<Course>> {
        return proceedFlow {
            apiDataSource.getCourseById(id).data.course.toCourse()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(2000)
        }
    }
}
