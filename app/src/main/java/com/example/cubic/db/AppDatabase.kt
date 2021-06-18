package com.example.cubic.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Measurement::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}