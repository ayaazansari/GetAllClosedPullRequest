package com.example.getallclosedpullrequest.repository

import com.example.getallclosedpullrequest.api.RetrofitInstance

class PullRequestRepository {
    suspend fun getPullRequest(
        owner: String,
        repo: String,
        pullRequestState: String,
        pageNumber: Int
    ) = RetrofitInstance.api.getPullRequest(owner, repo, pullRequestState, pageNumber)
}