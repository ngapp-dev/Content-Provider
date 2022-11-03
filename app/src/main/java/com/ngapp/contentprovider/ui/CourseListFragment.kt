package com.ngapp.contentprovider.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngapp.contentprovider.databinding.FragmentListCourseBinding
import com.ngapp.contentprovider.ui.adapter.CourseListAdapter
import com.ngapp.contentprovider.utils.ViewBindingFragment
import com.ngapp.contentprovider.utils.launchAndCollectIn

class CourseListFragment : ViewBindingFragment<FragmentListCourseBinding>(FragmentListCourseBinding::inflate) {
    private val viewModel: CourseListViewModel by viewModels()
    private lateinit var courseListAdapter: CourseListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        bindViewModel()
    }

    private fun initList() {
        courseListAdapter = CourseListAdapter()
        with(binding.courseList) {
            adapter = courseListAdapter
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
        }
    }

    private fun bindViewModel() {
        viewModel.loadList()
        viewModel.courseFlow.launchAndCollectIn(viewLifecycleOwner) { courses ->
            courseListAdapter.submitList(courses)
            binding.courseList.scrollToPosition(0)
        }
    }
}