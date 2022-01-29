package com.alexpaxom.gitsearch.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "users")
data class UserCard (

    @PrimaryKey
    @field:Json(name = "id")
    val id:Long ,

    @ColumnInfo(name = "login")
    @field:Json(name = "login")
    val login:String? = "",

    @ColumnInfo(name = "avatar_url")
    @field:Json(name = "avatar_url")
    val avatarUrl:String? = "",

    @ColumnInfo(name = "html_url")
    @field:Json(name = "html_url")
    val url:String? = "",

    @ColumnInfo(name = "name")
    @field:Json(name = "name")
    val name:String? = "",

    @ColumnInfo(name = "email")
    @field:Json(name = "email")
    val email:String? = "",

)
