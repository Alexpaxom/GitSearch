package com.alexpaxom.gitsearch.domain.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alexpaxom.gitsearch.R
import com.alexpaxom.gitsearch.data.cachedatabase.converters.UserIdentifierJsonConverter
import com.alexpaxom.homework_2.data.models.ListItem
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "repositories")
@TypeConverters(UserIdentifierJsonConverter::class)
data class RepositoryCard (
    @ColumnInfo(name = "typeId")
    override val typeId: Int = 0,

    @PrimaryKey
    @field:Json(name = "id")
    val id:Long,

    @ColumnInfo(name = "name")
    @field:Json(name = "name")
    val name:String? = null,

    @ColumnInfo(name = "description")
    @field:Json(name = "description")
    val description:String? = null,

    @ColumnInfo(name = "forks_count")
    @field:Json(name = "forks_count")
    val forksCount:Int? = null,

    @ColumnInfo(name = "owner")
    @field:Json(name = "owner")
    val owner: UserIdentifier? = null,
): Parcelable, ListItem
