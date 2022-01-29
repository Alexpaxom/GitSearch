package com.alexpaxom.gitsearch.domain.interactors.repositoriesdetails

import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.data.repositories.GitUsersRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class GetOwnerInfoInteractor(
    val store: BaseStore<RepositoryDetailsState, RepositoryDetailsEvent>
) {
    private val gitUsersRepository: GitUsersRepository = GitUsersRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getUserInfo(event: RepositoryDetailsEvent.GetRepositoryOwnerDetails) {
        gitUsersRepository.getUserById(event.ownerId, useCache = true, refreshCache = true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = {
                    store.processEvent(
                        RepositoryDetailsEvent.ResultRepositoryOwnerDetails(it)
                    )
                },
                onError = {
                    store.processEvent(
                        RepositoryDetailsEvent.GetInformationError(it)
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}