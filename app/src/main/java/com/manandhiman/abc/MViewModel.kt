package com.manandhiman.abc

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room

class MViewModel(application: Application): AndroidViewModel(application) {

  private val db: AppDatabase = Room.databaseBuilder(
    getApplication(),
    AppDatabase::class.java, "database-name",)
    .allowMainThreadQueries().build()

  private val dao = db.dao()


  private val _students = mutableStateOf(dao.getStudents())
  val students get() = _students.value.sortedBy { it.name }

  fun addStudent(inputName: String) {
    try {
      dao.addStudent(Student(name = inputName.trim()))
      _students.value = dao.getStudents()
    } catch (_: Exception) {

    }

  }

  fun deleteFromDB(student: Student) {
    dao.delete(student)
    _students.value = dao.getStudents()
  }

}