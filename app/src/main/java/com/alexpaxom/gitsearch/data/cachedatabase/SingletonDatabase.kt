package com.alexpaxom.gitsearch.data.cachedatabase

import androidx.room.Room
import com.alexpaxom.gitsearch.app.App

object SingletonDatabase {
    val cacheDatabase =  Room.databaseBuilder(
    App.application,
    CacheDatabase::class.java, "cacheDatabase"
    ).build()
}