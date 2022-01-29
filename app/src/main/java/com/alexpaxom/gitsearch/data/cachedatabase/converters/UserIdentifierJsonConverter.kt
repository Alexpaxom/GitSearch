package com.alexpaxom.gitsearch.data.cachedatabase.converters

import androidx.room.TypeConverter
import com.alexpaxom.gitsearch.domain.entities.UserIdentifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class UserIdentifierJsonConverter {
    @TypeConverter
    fun userIdentifierFromList(userIdentifier: UserIdentifier): String {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(UserIdentifier::class.java)
        val adapter = moshi.adapter<UserIdentifier>(type)

        return adapter.toJson(userIdentifier)
    }

    @TypeConverter
    fun userIdentifierFromString(serializedUserIdentifier:String): UserIdentifier {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(UserIdentifier::class.java)
        val adapter = moshi.adapter<UserIdentifier>(type)

        return adapter.fromJson(serializedUserIdentifier) ?: UserIdentifier()
    }
}
