package com.alexpaxom.gitsearch.domain.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
class UserIdentifier (
    @field:Json(name = "id")
    val id:Long = 0,
): Parcelable