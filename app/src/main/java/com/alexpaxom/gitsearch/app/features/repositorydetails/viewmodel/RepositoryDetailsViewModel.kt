package com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.interactors.repositoriesdetails.GetOwnerInfoInteractor

class RepositoryDetailsViewModel: ViewModel(),
    BaseStore<RepositoryDetailsState, RepositoryDetailsEvent> {

    private val _currentState: MutableLiveData<RepositoryDetailsState> = MutableLiveData()
    val viewState: LiveData<RepositoryDetailsState>
        get() = _currentState
    private val currentState: RepositoryDetailsState
        get() = _currentState.value ?: RepositoryDetailsState()

    private val getOwnerInfoInteractor: GetOwnerInfoInteractor = GetOwnerInfoInteractor(this)


    override fun processEvent(event: RepositoryDetailsEvent) {
        when(event) {
            is RepositoryDetailsEvent.GetRepositoryDetails -> {
                setState(
                    currentState.copy(
                        repositoryInfo = event.repositoryInfo,
                        isEmptyLoading = true
                    )
                )

                getOwnerInfoInteractor.getUserInfo(
                    RepositoryDetailsEvent.GetRepositoryOwnerDetails(
                        event.repositoryInfo.owner?.id ?: error("Bad owner repository id")
                    )
                )
            }
            //TODO Сделать централизованную обработку ошибок
            is RepositoryDetailsEvent.GetInformationError -> { Log.e("TEST", event.error.localizedMessage) }
            is RepositoryDetailsEvent.GetRepositoryOwnerDetails -> { error("Bad event in Store") }
            is RepositoryDetailsEvent.ResultRepositoryOwnerDetails -> {
                setState(
                    currentState.copy(
                        repositoryOwnerInfo = event.ownerInfo.data,
                        isEmptyLoading = event.ownerInfo is CacheWrapper.CachedData
                    )
                )
            }
        }
    }

    override fun setState(state: RepositoryDetailsState) {
        _currentState.value = state
    }

    override fun onCleared() {
        super.onCleared()
        getOwnerInfoInteractor.onDestroy()
    }
}