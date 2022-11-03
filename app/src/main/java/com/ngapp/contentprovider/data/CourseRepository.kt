package com.ngapp.contentprovider.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.ngapp.contentprovider.BuildConfig
import com.ngapp.contentprovider.custom_content_provider.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CourseRepository(
    private val context: Context
) {

    suspend fun getCourseList(): List<Course> = withContext(Dispatchers.IO) {
        val uri: Uri = Uri.parse(COURSES_URI)
        context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            getCoursesFromCursor(cursor)
        }.orEmpty()
    }

    private fun getCoursesFromCursor(cursor: Cursor): List<Course> {
        if (cursor.moveToFirst().not()) return emptyList()
        val list = mutableListOf<Course>()
        do {

            val idIndex = cursor.getColumnIndex(COLUMN_COURSE_ID)
            val id = cursor.getLong(idIndex)
            val titleIndex =
                cursor.getColumnIndex(COLUMN_COURSE_TITLE)
            val title = cursor.getString(titleIndex).orEmpty()

            list.add(
                Course(
                    id = id,
                    title = title
                )
            )
        } while (cursor.moveToNext())

        return list
    }

    companion object {
        private const val AUTHORITIES = "${BuildConfig.APPLICATION_ID}.provider"
        private const val PATH_COURSES = "courses"

        private const val COLUMN_COURSE_ID = "id"
        private const val COLUMN_COURSE_TITLE = "title"

        const val COURSES_URI = "content://$AUTHORITIES/$PATH_COURSES/"
    }

}