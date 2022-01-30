package com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate

import com.alexpaxom.gitsearch.app.baseelements.BaseState
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.domain.entities.UserCard

data class RepositoryDetailsState (
    // ui
    val repositoryInfo: RepositoryCard = RepositoryCard(id = 0),
    val repositoryOwnerInfo: UserCard = UserCard(id = 0),
    val isEmptyLoading: Boolean = false,

    // inner
    val hasInternetConnection: Boolean = true,
    val dataIsLoaded: Boolean = false,
): BaseState