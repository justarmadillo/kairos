package com.taha.kairos.authorization

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reports only validated internet access. A connected Wi-Fi network with a
 * captive portal (or no upstream internet) is deliberately considered offline.
 */
interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}

@Singleton
class ConnectivityNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    private val _isOnline = MutableStateFlow(hasValidatedInternet())
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateFromActiveNetworks()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            if (networkCapabilities.hasValidatedInternet()) {
                _isOnline.value = true
            } else {
                updateFromActiveNetworks()
            }
        }

        override fun onLost(network: Network) {
            updateFromActiveNetworks()
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun updateFromActiveNetworks() {
        _isOnline.value = hasValidatedInternet()
    }

    private fun hasValidatedInternet(): Boolean =
        connectivityManager.activeNetwork
            ?.let(connectivityManager::getNetworkCapabilities)
            ?.hasValidatedInternet() == true

    private fun NetworkCapabilities.hasValidatedInternet(): Boolean =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
