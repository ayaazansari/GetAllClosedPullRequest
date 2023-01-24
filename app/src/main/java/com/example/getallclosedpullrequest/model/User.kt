package com.example.getallclosedpullrequest.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar_url") var profileUrl: String?,
    @SerializedName("login") var name: String?
)