package com.ngapp.contentprovider.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngapp.contentprovider.custom_content_provider.Course
import com.ngapp.contentprovider.databinding.ItemCourseBinding

class CourseListAdapter : ListAdapter<Course, CourseListAdapter.Holder>(CourseDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(
            ItemCourseBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class Holder(
        private val binding: ItemCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            course: Course
        ) {
            binding.titleTextView.text = course.title
        }

    }

    class CourseDiffUtilCallBack : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }


}