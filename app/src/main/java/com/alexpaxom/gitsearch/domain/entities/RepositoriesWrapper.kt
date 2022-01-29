package com.alexpaxom.gitsearch.domain.entities

import com.squareup.moshi.Json

class RepositoriesWrapper {
    @field:Json(name = "items")
    val repositoriesCards: List<RepositoryCard> = listOf()
}