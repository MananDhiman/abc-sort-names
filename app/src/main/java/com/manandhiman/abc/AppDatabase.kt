package com.manandhiman.abc

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Student::class],
    version = 1,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
//    exportSchema = true
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun dao(): Dao
}