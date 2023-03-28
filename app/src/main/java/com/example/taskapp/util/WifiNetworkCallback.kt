package com.example.taskapp.util

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.widget.Toast

class WifiNetworkCallback (private val context: Context?): NetworkCallback(){
    override fun onLost(network: Network) {
        super.onLost(network)
        Toast.makeText(context, "WiFi is disconnected", Toast.LENGTH_SHORT).show()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Toast.makeText(context, "WiFi is connected", Toast.LENGTH_SHORT).show()

    }

}