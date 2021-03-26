package com.example.cubic.hanlders

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashSet

enum class Actions(val value: String) {
    Measure("0"),
    Disconnect("1")
}

class BluetoothHandler {
    private var bluetoothAdapter: BluetoothAdapter;
    private lateinit var connectionThread: ConnectThread;
    private var TAG = "Bluetooth Handler"
    private var observers = HashSet<Observer>();
    private var connectivityObservers = HashSet<Observer>();
    private var handler = Handler(Looper.getMainLooper())

    constructor() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public fun registerObserver(o: Observer) {
        this.observers.add(o);
    }

    public fun registerConnectivityObserver(o: Observer) {
        this.connectivityObservers.add(o);
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
                    Log.d(TAG, "Device Found:$deviceName $deviceHardwareAddress");
                    if (deviceName == "ESP32test") {
                        connectionThread = ConnectThread(device);
                        connectionThread.start();
                    }
                }
            }
        }
    }

    public fun startDiscovery(): Boolean {
        Log.d(TAG, "Looking for ESP32 device...")
        return this.bluetoothAdapter.startDiscovery();
    }

    public fun closeConnection() {
        this.connectionThread.writeMessage(Actions.Disconnect);
        this.connectionThread.cancel();
    }

    public fun requestMeasure() {
        this.connectionThread.writeMessage(Actions.Measure);
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
                handler.post(Runnable {
                    connectivityObservers.forEach { o -> o.update(null, true) }
                })
                Log.d(TAG, "Established connection successfully with ESP32")
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
                Log.e(TAG, "Error sending message to host", e)
            }
        }

        private fun manageConnectedSocket(socket: BluetoothSocket) {
            val mmInStream: InputStream = socket.inputStream
            val mmBuffer: ByteArray = ByteArray(1024)
            var numBytes: Int
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }
                var incomingString = (String(mmBuffer.copyOfRange(0, numBytes)));
                var floatValue = DecimalFormat("#.##").format(incomingString.toFloat());
                Log.d(TAG, "Incoming Message: $numBytes, $incomingString")
                handler.post(Runnable() {
                    observers.forEach { o -> o.update(null, floatValue) }
                })

            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
                this.interrupt();
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}