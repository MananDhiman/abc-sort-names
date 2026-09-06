package com.manandhiman.abc

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MViewModel(private val databaseHandler: DatabaseHandler) : ViewModel() {

    val activeList = mutableIntStateOf(0)
    fun setActiveList(newActiveList: Int) {
        activeList.intValue = newActiveList
        refreshLists()
    }

    private val _students = mutableStateOf(databaseHandler.getStudents(activeList.intValue))
    val students get() = _students.value.sortedBy { it.name }
    val studentsReversed get() = _students.value.sortedByDescending { it.name }
    val studentsNameLength get() = _students.value.sortedBy { it.name.length }

    private val _abcLists = mutableStateOf(databaseHandler.getLists())
    val abcLists get() = _abcLists.value


    fun addStudent(inputName: String) {
        val formattedName = inputName.trim().split(" ").joinToString(separator = " ") { it.capitalize() }

        try {
            viewModelScope.launch(Dispatchers.IO) {
                databaseHandler.addNewStudent(formattedName, activeList.intValue)
            }
            refreshLists()
        } catch (_: Exception) {

        }

        refreshLists()

    }

    fun addOrUpdateList(id: Int, listName: String = "Default Name") {
        if (id == 0) {
            viewModelScope.launch(Dispatchers.IO) {
                databaseHandler.addNewList(listName)
            }
        }

        refreshLists()
    }

    fun refreshLists(activeList: Int = this.activeList.intValue) {
        Log.d("tag db", "refresh called with $activeList")
        viewModelScope.launch(Dispatchers.IO) {
            _students.value = databaseHandler.getStudents(activeList)
            _abcLists.value = databaseHandler.getLists()
        }
    }

    fun deleteStudentFromDB(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseHandler.deleteStudent(student.name)
        }

        refreshLists()
    }

    fun deleteList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseHandler.deleteList(listId)
        }

        refreshLists()
    }
}