package com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val _internetConnection: MutableLiveData<Boolean> = MutableLiveData(true)
    val internetConnection:LiveData<Boolean> = _internetConnection

    fun setInternetConnection(hasConnection: Boolean) {
        _internetConnection.value = hasConnection
    }
}