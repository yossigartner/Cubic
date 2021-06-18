package com.example.cubic.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurement")
    fun getAll(): List<Measurement>

    @Insert
    fun insert(vararg measurement: Measurement)
}