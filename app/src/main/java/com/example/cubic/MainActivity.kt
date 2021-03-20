package com.example.cubic

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cubic.hanlders.BluetoothHandler

class MainActivity : AppCompatActivity() {
    val bluetoothHandler = BluetoothHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    fun discoverAndConnectToESPDevice() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(this.bluetoothHandler.receiver, filter)
        this.bluetoothHandler.startDiscovery();
    }
}