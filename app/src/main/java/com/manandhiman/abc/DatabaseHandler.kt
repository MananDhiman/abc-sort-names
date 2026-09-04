package com.manandhiman.abc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "database-name"
private const val DB_VER = 2

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VER) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Students.CREATE_TABLE_QUERY)
        db?.execSQL(ABCLists.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL(ABCLists.CREATE_TABLE_QUERY)
        db?.execSQL("ALTER TABLE ${Students.TABLE_NAME} ADD COLUMN ${Students.COLUMN_LIST_ID} TEXT")
        db?.execSQL("UPDATE ${Students.TABLE_NAME} SET ${Students.COLUMN_LIST_ID}=0")
    }

    fun getStudents(): List<Student> {
        val db = this.readableDatabase

        val list = mutableListOf<Student>()
        val query = "SELECT * FROM ${Students.TABLE_NAME} ORDER BY ${Students.COLUMN_NAME}"

        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {
                list.add(Student(
                    result.getString(0),
                    result.getInt(1)
                ))
            } while (result.moveToNext())
        }

        result.close()
        return list
     }

    fun getLists(): List<ABCList> {
        val db = this.readableDatabase

        val list = mutableListOf<ABCList>()
        val query = "SELECT * FROM ${ABCLists.TABLE_NAME}"

        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {
                list.add(ABCList(
                    result.getInt(0),
                    result.getString(1)
                ))
            } while (result.moveToNext())
        }

        result.close()
        return list
    }

    fun addNewStudent(formattedName: String, listId: Int = 0): Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(Students.COLUMN_NAME, formattedName)
            put(Students.COLUMN_LIST_ID, listId)
        }

        return db.insert(Students.TABLE_NAME, null, values)
    }

    fun addNewList(abcList: String): Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(ABCLists.COLUMN_NAME, abcList)
        }

        return db.insert(ABCLists.TABLE_NAME, null, values)
    }

    fun deleteList(listId: Int) {
        val db = this.writableDatabase
        val selection = "${ABCLists.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(listId.toString())
        db.delete(ABCLists.TABLE_NAME, selection, selectionArgs)
    }

    fun deleteStudent(name: String) {
        val db = this.writableDatabase
        val selection = "${Students.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(name)
        db.delete(Students.TABLE_NAME, selection, selectionArgs)
    }

    private object Students {
        const val TABLE_NAME = "students"
        const val COLUMN_NAME = "name"
        const val COLUMN_LIST_ID = "list_id"

        const val CREATE_TABLE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME TEXT PRIMARY KEY," +
                "$COLUMN_LIST_ID);"
    }

    private object ABCLists {
        const val TABLE_NAME = "abc_list"
        const val COLUMN_NAME = "name"
        const val COLUMN_ID = "id"

        const val CREATE_TABLE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "$COLUMN_NAME TEXT);"
    }
}