package com.alexpaxom.gitsearch.app.features.search.elementsofstate

sealed interface SearchEffect {
    class ShowError(val error: String): SearchEffect
}