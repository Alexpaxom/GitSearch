package com.alexpaxom.gitsearch.domain.entities

sealed class CacheWrapper<T>(val data: T) {
    class CachedData<T> (data: T): CacheWrapper<T>(data)
    class OriginalData<T> (data: T): CacheWrapper<T>(data)

    fun<R> mapWrapped(transform:(data: T) -> R): CacheWrapper<R> {
        return when(this) {
            is CachedData -> CachedData(transform(this.data))
            is OriginalData -> OriginalData(transform(this.data))
        }
    }
}