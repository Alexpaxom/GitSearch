package com.alexpaxom.gitsearch.data.remoteapi

import com.alexpaxom.gitsearch.domain.entities.RepositoriesWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiRequests {
    @GET("/search/repositories")
    fun getRepositories(
        @Query("q") search:String,
        @Query("page") page:String,
        @Query("per_page") perPage:String
    ): Call<RepositoriesWrapper>
}