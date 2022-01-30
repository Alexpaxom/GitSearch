package com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate

import com.alexpaxom.gitsearch.app.baseelements.BaseEvent
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.domain.entities.UserCard

sealed interface RepositoryDetailsEvent: BaseEvent {
    class GetRepositoryDetails(val repositoryInfo: RepositoryCard): RepositoryDetailsEvent
    class GetRepositoryOwnerDetails(val ownerId: Long): RepositoryDetailsEvent
    class ResultRepositoryOwnerDetails(val ownerInfo: CacheWrapper<UserCard>): RepositoryDetailsEvent
    class GetInformationError(val error: Throwable): RepositoryDetailsEvent
    class InternetConnectionEvent(val hasInternetConnection: Boolean): RepositoryDetailsEvent
}