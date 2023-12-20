package com.group4.gostudy.presentation.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.group4.gostudy.R
import com.group4.gostudy.databinding.FragmentClassesBinding
import com.group4.gostudy.model.PopularCourse
import com.group4.gostudy.presentation.classes.myclass.MyClassAdapter
import com.group4.gostudy.presentation.course.course.CourseAdapter
import com.group4.gostudy.presentation.detail.DetailCourseActivity
import com.group4.gostudy.presentation.home.DialogHomeNonLoginFragment
import com.group4.gostudy.utils.ApiException
import com.group4.gostudy.utils.hideKeyboard
import com.group4.gostudy.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassesFragment : Fragment() {
    private lateinit var binding: FragmentClassesBinding
    private val dialogFragment = DialogHomeNonLoginFragment()
    private val classesViewModel: ClassesViewModel by viewModel()

    private fun navigateToNonLoginFragment() {
        dialogFragment.show(childFragmentManager, "DialogHomeNonLoginFragment")
    }

    private val myClassAdapter: MyClassAdapter by lazy {
        MyClassAdapter {
            CourseAdapter { course: PopularCourse ->
                navigateToDetail(course)
            }
        }
    }

    private fun navigateToDetail(courses: PopularCourse) {
        DetailCourseActivity.startActivity(requireContext(), courses)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        navigateToNonLoginFragment()
        setMyClassRv()
        setSearchFeature()
        observeCourse()
    }

    private fun observeCourse() {
        classesViewModel.getCourse()
        classesViewModel.courses.observe(
            viewLifecycleOwner
        ) {
            it.proceedWhen(
                doOnLoading = {
                    binding.layoutStateCourse.root.isVisible =
                        true
                    binding.layoutStateCourse.animLoading.isVisible =
                        true
                    binding.layoutStateCourse.llAnimError.isVisible =
                        false
                    binding.rvListOfClass.isVisible =
                        false
                },
                doOnSuccess = {
                    binding.layoutStateCourse.root.isVisible =
                        true
                    binding.layoutStateCourse.animLoading.isVisible =
                        false
                    binding.layoutStateCourse.llAnimError.isVisible =
                        false
                    binding.rvListOfClass.isVisible =
                        true
                    it.payload?.let {
                        myClassAdapter.setData(it)
                    }
                },
                doOnError = {
                    binding.layoutStateCourse.root.isVisible =
                        true
                    binding.layoutStateCourse.animLoading.isVisible =
                        false
                    binding.layoutStateCourse.llAnimError.isVisible =
                        true
                    binding.rvListOfClass.isVisible =
                        false
                    if (it.exception is ApiException) {
                        binding.layoutStateCourse.tvError.isVisible =
                            true
                        binding.layoutStateCourse.tvError.text =
                            it.exception.getParsedError()?.message
                    }
                },
                doOnEmpty = {
                    binding.layoutStateCourse.root.isVisible =
                        true
                    binding.layoutStateCourse.animLoading.isVisible =
                        false
                    binding.layoutStateCourse.tvError.isVisible =
                        true
                    binding.layoutStateCourse.llAnimError.isVisible =
                        true
                    binding.rvListOfClass.isVisible =
                        false
                    binding.layoutStateCourse.tvError.text =
                        getString(R.string.text_no_data)
                    if (it.exception is ApiException) {
                        binding.layoutStateCourse.tvError.text =
                            it.exception.getParsedError()?.message
                    }
                }
            )
        }
    }

    private fun setSearchFeature() {
        binding.svCourse.setOnCloseListener() {
            hideKeyboard()
            classesViewModel.getCourse()
            false
        }

        binding.svCourse.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (!query.isNullOrEmpty()) {
                    classesViewModel.getCourse(search = query.trim())
                    false
                } else {
                    classesViewModel.getCourse() // Mengambil semua data course
                    false
                }
            }

            override fun onQueryTextChange(newQuery: String?): Boolean {
                return false
            }
        })
    }

    private fun setMyClassRv() {
        binding.rvListOfClass.apply {
            adapter = myClassAdapter
        }
    }
}
