package com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import junit.framework.TestCase

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryDetailsViewModelTest : TestCase() {

    lateinit var viewModelTest: RepositoryDetailsViewModel


    @Before
    public override fun setUp() {
        viewModelTest = RepositoryDetailsViewModel()
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

}