package com.example.cubic.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.cubic.R
import com.example.cubic.db.Measurement

class SavedMeasurementsAdapter(private val dataSet: List<Measurement>) :
    RecyclerView.Adapter<SavedMeasurementsAdapter.SingleMeasurementView>() {

    class SingleMeasurementView(view: View) : RecyclerView.ViewHolder(view) {
        val measurementSummary: TextView;

        init {
            measurementSummary = view.findViewById(R.id.singleMeasurementSummary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleMeasurementView {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.measurement_item, parent, false)

        return SingleMeasurementView(view);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: SingleMeasurementView, position: Int) {
        val measurement = dataSet[position];

        holder.measurementSummary.text = Html.fromHtml(
            "${measurement.measurementName}, ${measurement.measurementLocation}, ${measurement.measurementDate}, ${measurement.measurementValue}m&#xB2;",
            Html.FROM_HTML_MODE_COMPACT
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}