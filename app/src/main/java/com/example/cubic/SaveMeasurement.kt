package com.example.cubic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SaveMeasurement : FragmentActivity() {
    private var TAG = "Save Measurement Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_measurement)
        val measurementResult =
            intent.getFloatExtra(getString(R.string.EXTRA_MEASUREMENT_RESULT), 0f);
        Log.d(TAG, "$measurementResult")
        var measurementDimensionTextView = findViewById<TextView>(R.id.measurementDimesionInput)
        measurementDimensionTextView.text = "$measurementResult SQUARED METER"
    }

    public fun rescan(view: View) {
        finish()
    }

    public fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment();
        newFragment.show(supportFragmentManager, "datePicker")
    }
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity!!, this, year, month, day)
    }

    @SuppressLint("NewApi")
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val measurementDateInput = activity!!.findViewById<TextView>(R.id.measurementDateInput)
        val chosenDate = LocalDate.of(year, month, day);
        measurementDateInput.text = chosenDate.format(DateTimeFormatter.ISO_DATE);
    }
}