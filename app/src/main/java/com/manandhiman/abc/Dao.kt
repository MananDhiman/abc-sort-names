package com.manandhiman.abc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Query("SELECT * FROM students")
    fun getStudents(): List<Student>

    @Insert
    fun addStudent(student: Student)

    @Delete
    fun delete(student: Student)

}