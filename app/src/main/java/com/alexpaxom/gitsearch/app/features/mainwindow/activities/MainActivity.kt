package com.alexpaxom.gitsearch.app.features.mainwindow.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.alexpaxom.gitsearch.app.App
import com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel.MainActivityViewModel
import com.alexpaxom.gitsearch.app.helpers.ErrorsHandler
import com.alexpaxom.gitsearch.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var connectManagerCallback: ConnectManagerCallback? = null

    private val errorsHandler: ErrorsHandler = ErrorsHandler()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel = lazy {
        ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent
            .getScreenComponent()
            .create()
            .inject(this)

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainActivityViewModel.value.setInternetConnection(hasInternetConnection())

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectManagerCallback = ConnectManagerCallback()
        connectManagerCallback?.let{
            cm.registerNetworkCallback(networkRequest, it)
        }

    }

    private fun hasInternetConnection(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cm.allNetworks
                .mapNotNull(cm::getNetworkCapabilities)
                .any { capabilities -> capabilities.hasCapability(NET_CAPABILITY_INTERNET) }
        } else {
            cm.activeNetworkInfo?.isConnected == true
        }
    }

    override fun onDestroy() {
        connectManagerCallback?.let{
            try {
                val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                cm.unregisterNetworkCallback(it)
            } catch (exception: IllegalArgumentException) {
                errorsHandler.processError(exception)
            }
        }

        super.onDestroy()

        _binding = null
    }

    inner class ConnectManagerCallback: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread {
                mainActivityViewModel.value.setInternetConnection(true)
                binding.internetConnectionError.isVisible = false
            }
        }
        override fun onLost(network: Network) {
            runOnUiThread {
                mainActivityViewModel.value.setInternetConnection(false)
                binding.internetConnectionError.isVisible = true
            }
        }
    }
}