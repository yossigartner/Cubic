package com.example.cubic

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class SaveMeasurement : Activity() {
    private var TAG = "Save Measurement Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_measurement)
        val measurementResult = intent.getFloatExtra(getString(R.string.EXTRA_MEASUREMENT_RESULT),0f);
        Log.d(TAG,"$measurementResult")
        var measurementDimensionTextView = findViewById<TextView>(R.id.measurementDimesionInput)
        measurementDimensionTextView.text ="$measurementResult SQUARED METER"
    }

    public fun rescan(view:View) {
        finish()
    }
}