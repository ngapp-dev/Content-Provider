package com.ngapp.contentprovider.custom_content_provider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.ngapp.contentprovider.BuildConfig
import com.squareup.moshi.Moshi

class NgappContentProvider : ContentProvider() {

    private lateinit var coursesPrefs: SharedPreferences
    private val courseAdapter = Moshi.Builder().build().adapter(Course::class.java)

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITIES, PATH_COURSES, TYPE_COURSES)
        addURI(AUTHORITIES, "$PATH_COURSES/#", TYPE_COURSE_ID)
    }

    override fun onCreate(): Boolean {
        coursesPrefs = context!!.getSharedPreferences("course_shared_prefs", Context.MODE_PRIVATE)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            TYPE_COURSES -> getAllCursesCursor()
            TYPE_COURSE_ID -> getCourseByIdCursor(uri)
            else -> null
        }
    }

    private fun getAllCursesCursor(): Cursor {
        val allCourses = coursesPrefs.all.mapNotNull {
            val courseJsonString = it.value as String
            courseAdapter.fromJson(courseJsonString)
        }
        val cursor = MatrixCursor(arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_TITLE))
        allCourses.forEach { course ->
            cursor.newRow()
                .add(course.id)
                .add(course.title)
        }
        return cursor
    }

    private fun getCourseByIdCursor(uri: Uri): Cursor {
        val courseId = uri.lastPathSegment?.toLongOrNull()?.toString()
        val courseJsonString = coursesPrefs.getString(courseId, "") as String
        courseAdapter.fromJson(coursesPrefs.contains(courseId) as String)
        val cursor = MatrixCursor(arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_TITLE))
        if (coursesPrefs.contains(courseId)) {
            val course = coursesPrefs.getLong(courseId, 0)
            cursor.newRow()
        } else {

        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        values ?: return null
        return when (uriMatcher.match(uri)) {
            TYPE_COURSES -> saveCourse(values)
            else -> null
        }
    }


    private fun saveCourse(contentValues: ContentValues): Uri? {
        val id = contentValues.getAsLong(COLUMN_COURSE_ID) ?: return null
        val title = contentValues.getAsString(COLUMN_COURSE_TITLE) ?: return null
        val course = Course(id, title)
        coursesPrefs.edit()
            .putString(id.toString(), courseAdapter.toJson(course))
            .apply()

        return Uri.parse("content://$AUTHORITIES/$PATH_COURSES/$id")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.match(uri)) {
            TYPE_COURSE_ID -> deleteCourse(uri)
            TYPE_COURSES -> deleteAllCourses()
            else -> 0
        }
    }

    private fun deleteCourse(uri: Uri): Int {
        val courseId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (coursesPrefs.contains(courseId)) {
            coursesPrefs.edit()
                .remove(courseId)
                .apply()
            1
        } else {
            0
        }
    }

    private fun deleteAllCourses(): Int {
        return if (coursesPrefs.all.isNotEmpty()) {
            coursesPrefs.edit()
                .clear()
                .apply()
            1
        } else {
            0
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        values ?: return 0
        return when (uriMatcher.match(uri)) {
            TYPE_COURSE_ID -> updateCourse(uri, values)
            else -> 0
        }
    }

    private fun updateCourse(uri: Uri, contentValues: ContentValues): Int {
        val userId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (coursesPrefs.contains(userId)) {
            saveCourse(contentValues)
            1
        } else {
            0
        }
    }
    companion object {
        private const val AUTHORITIES = "${BuildConfig.APPLICATION_ID}.provider"

        private const val PATH_COURSES = "courses"

        private const val TYPE_COURSES = 1
        private const val TYPE_COURSE_ID = 11

        private const val COLUMN_COURSE_ID = "id"
        private const val COLUMN_COURSE_TITLE = "title"

    }
}