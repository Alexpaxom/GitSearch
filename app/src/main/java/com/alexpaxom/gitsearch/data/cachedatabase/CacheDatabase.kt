package com.alexpaxom.gitsearch.data.cachedatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexpaxom.gitsearch.data.cachedatabase.daos.RepositoryCardDAO
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard

@Database(
    entities = [RepositoryCard::class],
    version = 1,
    exportSchema = false
)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun repositoriesDao(): RepositoryCardDAO
}
