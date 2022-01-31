package com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEffect
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.app.helpers.ErrorsHandler
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.domain.entities.UserCard
import com.alexpaxom.gitsearch.domain.entities.UserIdentifier
import com.alexpaxom.gitsearch.domain.interactors.repositoriesdetails.GetOwnerInfoInteractor
import io.mockk.*
import junit.framework.TestCase

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryDetailsViewModelTest : TestCase() {

    lateinit var viewModelTest: RepositoryDetailsViewModel
    lateinit var getOwnerInfoInteractor: GetOwnerInfoInteractor
    lateinit var errorsHandler: ErrorsHandler


    @Before
    public override fun setUp() {
        getOwnerInfoInteractor = mockk(relaxed = true)
        errorsHandler = mockk(relaxed = true)

        viewModelTest = RepositoryDetailsViewModel(
            getOwnerInfoInteractor = getOwnerInfoInteractor,
            errorsHandler = errorsHandler,
        )

        clearAllMocks()
        every { getOwnerInfoInteractor.getUserInfo(any(), any()) } returns Unit

    }

    @UiThreadTest
    @Test
    fun lostInternetConnectionEvent() {
        viewModelTest.processEvent(RepositoryDetailsEvent.InternetConnectionEvent(
            false
        ))

        val effect = viewModelTest.effect.value?.getContentIfNotHandled()

        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                hasInternetConnection = false
            ) && effect == null
        )
    }

    @UiThreadTest
    @Test
    fun returnInternetConnectionEvent() {
        viewModelTest.processEvent(RepositoryDetailsEvent.InternetConnectionEvent(
            false
        ))

        viewModelTest.processEvent(RepositoryDetailsEvent.InternetConnectionEvent(
            true
        ))

        val effect = viewModelTest.effect.value?.getContentIfNotHandled()

        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                hasInternetConnection = true
            )
                    && effect != null
        )
    }

    @UiThreadTest
    @Test
    fun internetConnectionNoChangedEvent() {
        viewModelTest.processEvent(RepositoryDetailsEvent.InternetConnectionEvent(
            true
        ))


        assertTrue(
            viewModelTest.effect.value?.getContentIfNotHandled() == null
        )
    }

    @UiThreadTest
    @Test
    fun getRepositoryDetailsEvent() {

        val repositoryCard = RepositoryCard(
            id=0,
            owner = UserIdentifier(0),
        )

        viewModelTest.processEvent(RepositoryDetailsEvent.GetRepositoryDetails(
            repositoryCard
        ))

        verify { getOwnerInfoInteractor.getUserInfo(any(), any()) }

        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                repositoryInfo = repositoryCard,
                isEmptyLoading = true,
                dataIsLoaded = false
            )
        )
    }

    @UiThreadTest
    @Test
    fun getRepositoryDetailsEventWithoutOwnerId() {

        val repositoryCard = RepositoryCard(
            id=0,
            //owner = UserIdentifier(0),
        )

        viewModelTest.processEvent(RepositoryDetailsEvent.GetRepositoryDetails(
            repositoryCard
        ))

        verify { errorsHandler.processError(any(), any()) }
        verify(exactly = 0) { getOwnerInfoInteractor.getUserInfo(any(), any()) }

        assertTrue(
            viewModelTest.viewState.value == null
        )
    }

    @UiThreadTest
    @Test
    fun getRepositoryDetailsNoInternetEvent() {
        viewModelTest.processEvent(RepositoryDetailsEvent.InternetConnectionEvent(
            false
        ))

        val repositoryCard = RepositoryCard(
            id=0,
            owner = UserIdentifier(0),
        )

        viewModelTest.processEvent(RepositoryDetailsEvent.GetRepositoryDetails(
            repositoryCard,
        ))

        verify(exactly = 0) { getOwnerInfoInteractor.getUserInfo(any(), any()) }
    }

    @UiThreadTest
    @Test
    fun getRepositoryOwnerDetailsEvent() {

        viewModelTest.processEvent(RepositoryDetailsEvent.GetRepositoryOwnerDetails(
            0
        ))

        verify { errorsHandler.processError(any(), any()) }
    }

    @UiThreadTest
    @Test
    fun getInformationErrorEvent() {

        viewModelTest.processEvent(RepositoryDetailsEvent.GetInformationError(
            Throwable("throwable error")
        ))

        verify { errorsHandler.processError(any(), any()) }

        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                dataIsLoaded = false
            )
        )
        val effect = viewModelTest.effect.value?.getContentIfNotHandled()

        assertTrue(
            effect != null && effect is RepositoryDetailsEffect.ShowError
        )
    }

    @UiThreadTest
    @Test
    fun resultRepositoryOwnerDetailsEventWithApiData() {

        val ownerInfo = UserCard(id=0)

        viewModelTest.processEvent(RepositoryDetailsEvent.ResultRepositoryOwnerDetails(
            CacheWrapper.OriginalData(ownerInfo)
        ))


        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                repositoryOwnerInfo = ownerInfo,
                isEmptyLoading = false,
                dataIsLoaded = true
            )
        )
    }

    @UiThreadTest
    @Test
    fun resultRepositoryOwnerDetailsEventWithCachedData() {

        val ownerInfo = UserCard(id=0)

        viewModelTest.processEvent(RepositoryDetailsEvent.ResultRepositoryOwnerDetails(
            CacheWrapper.CachedData(ownerInfo)
        ))


        assertTrue(
            viewModelTest.viewState.value == RepositoryDetailsState(
                repositoryOwnerInfo = ownerInfo,
                isEmptyLoading = true,
                dataIsLoaded = true
            )
        )
    }

}