package com.alexpaxom.gitsearch.data.remoteapi

import com.alexpaxom.gitsearch.domain.entities.UserCard
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiUsers {
    @GET("/user/{user_id}")
    fun getUserById(
        @Path("user_id") userId: Long,
    ): Call<UserCard>
}