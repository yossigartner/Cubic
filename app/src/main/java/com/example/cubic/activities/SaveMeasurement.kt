package com.example.cubic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.JsonReader
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.cubic.db.AppDatabase
import com.example.cubic.db.Measurement
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SaveMeasurement : FragmentActivity() {
    private var TAG = "Save Measurement Activity"
    private lateinit var measurementNameEditText: EditText;
    private lateinit var measurementDateTextView: TextView
    private lateinit var measurementLocationEditText: EditText
    private lateinit var measurementDimensionTextView: TextView
    private var handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_measurement)
        measurementNameEditText = findViewById<EditText>(R.id.measurementNameInput)
        measurementDateTextView = findViewById<TextView>(R.id.measurementDateInput)
        measurementLocationEditText = findViewById<EditText>(R.id.measurementLocationInput)
        measurementDimensionTextView = findViewById<TextView>(R.id.measurementDimesionInput)
        val measurementResult =
            intent.getFloatExtra(getString(R.string.EXTRA_MEASUREMENT_RESULT), 0f);
        Log.d(TAG, "$measurementResult")
        measurementDimensionTextView.text = "$measurementResult SQUARED METER"
    }

    public fun rescan(view: View) {
        finish()
    }

    public fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment();
        newFragment.show(supportFragmentManager, "datePicker")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun onConfirmButtonClick(v: View) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "measurements"
        ).allowMainThreadQueries().build()
        Log.d(TAG, db.measurementDao().getAll().size.toString())
        val measurementName = this.measurementNameEditText.text.toString();
        val measurementDate = this.measurementDateTextView.text.toString();
        val measurementLocation = this.measurementLocationEditText.text.toString();
        val measurementValue =
            intent.getFloatExtra(getString(R.string.EXTRA_MEASUREMENT_RESULT), 0f);
        val measurement = Measurement(
            UUID.randomUUID().toString(),
            measurementName,
            measurementDate,
            measurementLocation,
            measurementValue
        )
        db.measurementDao().insert(measurement)
        finish()
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