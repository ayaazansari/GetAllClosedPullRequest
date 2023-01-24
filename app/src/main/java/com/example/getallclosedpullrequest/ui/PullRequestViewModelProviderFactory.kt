package com.example.getallclosedpullrequest.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.getallclosedpullrequest.repository.PullRequestRepository

class PullRequestViewModelProviderFactory(
    private val app:Application,
    private val pullRequestRepository:PullRequestRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PullRequestViewModel(app,pullRequestRepository) as T
    }
}