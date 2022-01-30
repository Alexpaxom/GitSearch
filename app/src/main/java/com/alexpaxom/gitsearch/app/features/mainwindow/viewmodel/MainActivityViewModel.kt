package com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import javax.inject.Inject
import javax.inject.Singleton

@ScreenScope
class MainActivityViewModel @Inject constructor(): ViewModel() {
    private val _internetConnection: MutableLiveData<Boolean> = MutableLiveData(true)
    val internetConnection:LiveData<Boolean> = _internetConnection

    fun setInternetConnection(hasConnection: Boolean) {
        _internetConnection.value = hasConnection
    }
}