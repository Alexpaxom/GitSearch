package com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate

sealed interface RepositoryDetailsEffect {
    object ReloadData: RepositoryDetailsEffect
    class ShowError(val error: String): RepositoryDetailsEffect
}