package com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEffect
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.app.helpers.ErrorsHandler
import com.alexpaxom.gitsearch.app.helpers.LiveDataEvent
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.interactors.repositoriesdetails.GetOwnerInfoInteractor

class RepositoryDetailsViewModel : ViewModel(),
    BaseStore<RepositoryDetailsState, RepositoryDetailsEvent> {

    private val _currentState: MutableLiveData<RepositoryDetailsState> = MutableLiveData()
    val viewState: LiveData<RepositoryDetailsState>
        get() = _currentState
    private val currentState: RepositoryDetailsState
        get() = _currentState.value ?: RepositoryDetailsState()

    private val _effect: MutableLiveData<LiveDataEvent<RepositoryDetailsEffect>> = MutableLiveData()
    val effect: LiveData<LiveDataEvent<RepositoryDetailsEffect>>
        get() = _effect


    private val getOwnerInfoInteractor: GetOwnerInfoInteractor = GetOwnerInfoInteractor(this)

    private val errorsHandler: ErrorsHandler = ErrorsHandler()


    override fun processEvent(event: RepositoryDetailsEvent) {
        when (event) {
            is RepositoryDetailsEvent.GetRepositoryDetails -> {
                val userId = event.repositoryInfo.owner?.id

                if (userId == null) {
                    errorsHandler.processError(Throwable("Bad owner repository id"))
                    return
                }

                if (!currentState.hasInternetConnection)
                    return

                setState(
                    currentState.copy(
                        repositoryInfo = event.repositoryInfo,
                        isEmptyLoading = true,
                        dataIsLoaded = false
                    )
                )

                getOwnerInfoInteractor.getUserInfo(
                    RepositoryDetailsEvent.GetRepositoryOwnerDetails(
                        userId
                    )
                )
            }

            is RepositoryDetailsEvent.GetInformationError -> {
                errorsHandler.processError(event.error)

                _effect.value =
                    LiveDataEvent(RepositoryDetailsEffect.ShowError(event.error.localizedMessage))

                setState(
                    currentState.copy(
                        dataIsLoaded = false
                    )
                )
            }

            is RepositoryDetailsEvent.GetRepositoryOwnerDetails ->
                errorsHandler.processError(Throwable("Bad event in Store"))

            is RepositoryDetailsEvent.ResultRepositoryOwnerDetails -> {
                setState(
                    currentState.copy(
                        repositoryOwnerInfo = event.ownerInfo.data,
                        isEmptyLoading = event.ownerInfo is CacheWrapper.CachedData,
                        dataIsLoaded = true
                    )
                )
            }
            is RepositoryDetailsEvent.InternetConnectionEvent -> {
                if (event.hasInternetConnection == currentState.hasInternetConnection)
                    return

                setState(
                    currentState.copy(
                        isEmptyLoading = false,
                        hasInternetConnection = event.hasInternetConnection
                    )
                )

                if (event.hasInternetConnection)
                    _effect.value = LiveDataEvent(RepositoryDetailsEffect.ReloadData)
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