package com.example.cubic

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cubic.hanlders.BluetoothHandler
import java.util.*

class ObserverTextView(private val textView: TextView) : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg != null) {
            textView.text = arg.toString();
        }
    }
}

class ConnectivityObserver(private val button: Button) : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg != null) {
            button.isEnabled = arg as Boolean
        }
    }
}

class MainActivity : Activity() {
    val bluetoothHandler = BluetoothHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val resultView = ObserverTextView(findViewById<TextView>(R.id.resultTextView))
        var measureButton = ConnectivityObserver(findViewById<Button>(R.id.measureButton))
        bluetoothHandler.registerObserver(resultView);
        bluetoothHandler.registerConnectivityObserver(measureButton);
        if (ContextCompat.checkSelfPermission(baseContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        } else {
            discoverAndConnectToESPDevice()
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
}