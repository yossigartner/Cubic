package com.example.cubic.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity
data class Measurement(
    @PrimaryKey val uid: String,
    @ColumnInfo() val measurementName: String,
    @ColumnInfo() val measurementDate: String,
    @ColumnInfo() val measurementLocation: String,
    @ColumnInfo() val measurementValue: Float
)