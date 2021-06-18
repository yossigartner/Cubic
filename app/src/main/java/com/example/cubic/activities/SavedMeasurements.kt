package com.example.cubic.activities

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.cubic.R
import com.example.cubic.adapters.SavedMeasurementsAdapter
import com.example.cubic.db.AppDatabase

class SavedMeasurements : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_measurements)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "measurements"
        ).allowMainThreadQueries().build()
        val allMeasurements = db.measurementDao().getAll();
        val savedMeasurementAdapter = SavedMeasurementsAdapter(allMeasurements);
        val recyclerView: RecyclerView = findViewById(R.id.measurementsRecyclerView)
        recyclerView.adapter = savedMeasurementAdapter;
    }
}