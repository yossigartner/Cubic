package com.example.cubic.hanlders

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

enum class Actions(val value: String) {
    Measure("0"),
    Disconnect("1")
}

class BluetoothHandler {
    private var bluetoothAdapter: BluetoothAdapter;
    private lateinit var connectionThread: ConnectThread;

    constructor() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    if (deviceName == "ESP32test") {
                        connectionThread = ConnectThread(device);
                        connectionThread.start();
                    }
                    Log.d("Bluetooth Handler", "Device Found:$deviceName $deviceHardwareAddress");
                }
            }
        }
    }

    public fun startDiscovery(): Boolean {
        return this.bluetoothAdapter.startDiscovery();
    }

    public fun closeConnection() {
        this.connectionThread.writeMessage(Actions.Disconnect)
        this.connectionThread.cancel();
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                this.manageConnectedSocket(socket)
            }
        }

        public fun writeMessage(messageCode: Actions) {
            val mmOutStream: OutputStream? = mmSocket?.outputStream
            try {
                mmOutStream?.write(messageCode.value.toByteArray())
            } catch (e: IOException) {
                Log.e("Bluetooth Handler", "Error sending message to host", e)
            }
        }

        private fun manageConnectedSocket(socket: BluetoothSocket) {
            val mmInStream: InputStream = socket.inputStream
            val mmBuffer: ByteArray = ByteArray(1024)
            var numBytes: Int
            Log.d("Bluetooth Handler", "Start connection  with socket")
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("Bluetooth Handler", "Input stream was disconnected", e)
                    break
                }
                Log.d("Bluetooth Handler", "$numBytes, ${(String(mmBuffer.copyOfRange(0, numBytes)))}")
                writeMessage(Actions.Measure);
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("Bluetooth Handler", "Could not close the client socket", e)
            }
        }
    }
}