package com.manandhiman.abc

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dao(): Dao
}