package com.example.getallclosedpullrequest.model

import com.google.gson.annotations.SerializedName

data class PullRequest(
    var title: String?,
    @SerializedName("created_at")
    var createdDate: String?,
    @SerializedName("closed_at")
    var closedDate: String?,
    var user: User?
)