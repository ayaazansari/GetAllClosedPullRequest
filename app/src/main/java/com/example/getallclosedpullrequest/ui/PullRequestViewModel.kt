package com.example.getallclosedpullrequest.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getallclosedpullrequest.model.PullRequest
import com.example.getallclosedpullrequest.repository.PullRequestRepository
import com.example.getallclosedpullrequest.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

typealias PullRequestList = MutableList<PullRequest>

class PullRequestViewModel(
    val pullRequestRepository: PullRequestRepository
) : ViewModel() {
    val pullRequestList = MutableLiveData<Resource<PullRequestList>>()
    var pageNumber = 1
    var pullRequestListResponse: PullRequestList? = null

//    init{
//        getPullRequest("oppia","oppia-android","closed")
//    }

    fun refresh() {
        getPullRequest("oppia", "oppia-android", "closed")
    }

    private fun getPullRequest(owner: String, repo: String, pullRequestState: String) =
        viewModelScope.launch {
            pullRequestList.postValue(Resource.Loading())
            val response =
                pullRequestRepository.getPullRequest(owner, repo, pullRequestState, pageNumber)
            pullRequestList.postValue(handleResponse(response))
        }

    private fun handleResponse(response: Response<PullRequestList>): Resource<PullRequestList> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if (pullRequestListResponse == null) {
                    pullRequestListResponse = resultResponse
                } else {
                    val oldList = pullRequestListResponse
                    val newList = resultResponse
                    oldList?.addAll(newList)
                }
                return Resource.Success(pullRequestListResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}