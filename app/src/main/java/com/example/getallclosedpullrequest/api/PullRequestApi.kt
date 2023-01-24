package com.example.getallclosedpullrequest.api

import com.example.getallclosedpullrequest.model.PullRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PullRequestApi {

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequest(
        @Path("owner")
        owner: String,
        @Path("repo")
        repo: String,
        @Query("state")
        pullRequestState: String = "closed",
        @Query("page")
        pageNumber: Int = 1
    ): Response<MutableList<PullRequest>>
}