package com.alexpaxom.gitsearch.domain.entities

sealed class CacheWrapper<T>(val data: T) {
    class CachedData<T> (data: T): CacheWrapper<T>(data)
    class OriginalData<T> (data: T): CacheWrapper<T>(data)
}