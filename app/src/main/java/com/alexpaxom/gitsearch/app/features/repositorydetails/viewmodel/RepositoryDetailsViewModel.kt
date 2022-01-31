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
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.interactors.repositoriesdetails.GetOwnerInfoInteractor
import javax.inject.Inject

@ScreenScope
class RepositoryDetailsViewModel @Inject constructor(
    private val getOwnerInfoInteractor: GetOwnerInfoInteractor,
    private val errorsHandler: ErrorsHandler
): ViewModel(),
    BaseStore<RepositoryDetailsState, RepositoryDetailsEvent> {

    private val _currentState: MutableLiveData<RepositoryDetailsState> = MutableLiveData()
    val viewState: LiveData<RepositoryDetailsState>
        get() = _currentState
    private val currentState: RepositoryDetailsState
        get() = _currentState.value ?: RepositoryDetailsState()

    private val _effect: MutableLiveData<LiveDataEvent<RepositoryDetailsEffect>> = MutableLiveData()
    val effect: LiveData<LiveDataEvent<RepositoryDetailsEffect>>
        get() = _effect


    override fun processEvent(event: RepositoryDetailsEvent) {
        when (event) {
            is RepositoryDetailsEvent.GetRepositoryDetails -> processGetRepositoryDetailsEvent(event)
            is RepositoryDetailsEvent.GetInformationError -> processGetInformationErrorEvent(event)

            is RepositoryDetailsEvent.GetRepositoryOwnerDetails ->
                errorsHandler.processError(Throwable("Bad event in Store"))

            is RepositoryDetailsEvent.ResultRepositoryOwnerDetails ->
                processResultRepositoryOwnerDetailsEvent(event)

            is RepositoryDetailsEvent.InternetConnectionEvent ->
                processInternetConnectionEventEvent(event)
        }
    }

    private fun processGetRepositoryDetailsEvent(event: RepositoryDetailsEvent.GetRepositoryDetails) {
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
            event = RepositoryDetailsEvent.GetRepositoryOwnerDetails(
                userId
            ),
            store = this
        )
    }

    private fun processGetInformationErrorEvent(event: RepositoryDetailsEvent.GetInformationError) {
        errorsHandler.processError(event.error)

        _effect.value =
            LiveDataEvent(RepositoryDetailsEffect.ShowError(
                error = event.error.localizedMessage ?:
                event.error.message ?:
                "Error when load repository details!"
            ))

        setState(
            currentState.copy(
                dataIsLoaded = false
            )
        )
    }

    private fun processResultRepositoryOwnerDetailsEvent(
        event: RepositoryDetailsEvent.ResultRepositoryOwnerDetails
    ) {
        setState(
            currentState.copy(
                repositoryOwnerInfo = event.ownerInfo.data,
                isEmptyLoading = event.ownerInfo is CacheWrapper.CachedData,
                dataIsLoaded = true
            )
        )
    }

    private fun processInternetConnectionEventEvent(
        event: RepositoryDetailsEvent.InternetConnectionEvent
    ) {
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

    override fun setState(state: RepositoryDetailsState) {
        _currentState.value = state
    }

    override fun onCleared() {
        super.onCleared()
        getOwnerInfoInteractor.onDestroy()
    }
}