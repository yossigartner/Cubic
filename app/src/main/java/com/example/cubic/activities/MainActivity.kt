package com.example.cubic.activities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cubic.R
import com.example.cubic.hanlders.BluetoothHandler
import java.util.*

class ObserverTextView(private val textView: TextView) : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg != null) {
            textView.text = arg.toString();
        }
    }
}

class ObserverResultLayout(private val constraintLayout: ConstraintLayout) : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg != null) {
            constraintLayout.visibility = View.VISIBLE;
        }
    }
}

class ConnectivityObserver(private val button: Button) : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg != null) {
            button.isEnabled = arg as Boolean
            var backgroundTransition = button.background as TransitionDrawable;
            backgroundTransition.startTransition(200)
            button.setTextColor(Color.parseColor("#ffffff"))
        }
    }
}

class MainActivity : Activity() {
    val bluetoothHandler = BluetoothHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val resultTextView = ObserverTextView(findViewById<TextView>(R.id.resultTextView))
        var resultLayout = ObserverResultLayout(findViewById<ConstraintLayout>(R.id.measurementResultLayout))
        var measureButton = ConnectivityObserver(findViewById<Button>(R.id.measureButton))
        bluetoothHandler.registerObserver(resultTextView);
        bluetoothHandler.registerObserver(resultLayout);
        bluetoothHandler.registerConnectivityObserver(measureButton);
        if (ContextCompat.checkSelfPermission(baseContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        } else {
            // TODO: remove comment, remove mocks
            //discoverAndConnectToESPDevice()

            // MOCKS
            measureButton.update(null,true);
            val result = 25.666
            resultLayout.update(null,result);
            resultTextView.update(null,result)

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            discoverAndConnectToESPDevice()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this.bluetoothHandler.receiver)
        this.bluetoothHandler.closeConnection()
    }

    public fun onMeasureButtonClick(view: View) {
        this.bluetoothHandler.requestMeasure();
    }

    private fun discoverAndConnectToESPDevice() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(this.bluetoothHandler.receiver, filter)
        this.bluetoothHandler.startDiscovery();
    }

    public fun onSaveMeasureButtonClick(view:View) {
        val resultTextView = findViewById<TextView>(R.id.resultTextView);
        val intent = Intent(this,SaveMeasurement::class.java).apply {
            putExtra(getString(R.string.EXTRA_MEASUREMENT_RESULT),resultTextView.text.toString().toFloat())
        }
        startActivity(intent);
    }
}