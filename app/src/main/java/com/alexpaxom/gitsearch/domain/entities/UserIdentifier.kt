package com.alexpaxom.gitsearch.domain.entities

import com.squareup.moshi.Json

class UserIdentifier (
    @field:Json(name = "id")
    val id:Long = 0,
)