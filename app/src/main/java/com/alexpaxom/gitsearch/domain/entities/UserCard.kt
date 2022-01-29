package com.alexpaxom.gitsearch.domain.entities

import com.squareup.moshi.Json

data class UserCard (
    @field:Json(name = "id")
    val id:Long = 0,

    @field:Json(name = "login")
    val login:String = "",

    @field:Json(name = "avatar_url")
    val avatarUrl:String = "",

    @field:Json(name = "score")
    val score:Double = 0.0,

    @field:Json(name = "html_url")
    val url:String = "",

    @field:Json(name = "type")
    val type:String = "User",
)
