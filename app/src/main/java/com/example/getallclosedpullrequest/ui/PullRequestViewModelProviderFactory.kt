package com.example.getallclosedpullrequest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.getallclosedpullrequest.repository.PullRequestRepository

class PullRequestViewModelProviderFactory(
    val pullRequestRepository:PullRequestRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PullRequestViewModel(pullRequestRepository) as T
    }
}