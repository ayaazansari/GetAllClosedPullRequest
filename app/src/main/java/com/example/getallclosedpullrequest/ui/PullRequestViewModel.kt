package com.example.getallclosedpullrequest.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getallclosedpullrequest.model.PullRequest
import com.example.getallclosedpullrequest.repository.PullRequestRepository
import com.example.getallclosedpullrequest.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

typealias PullRequestList = MutableList<PullRequest>

class PullRequestViewModel(
    app:Application,
    val pullRequestRepository: PullRequestRepository
) : AndroidViewModel(app) {
    val pullRequestList = MutableLiveData<Resource<PullRequestList>>()
    var pageNumber = 1
    var pullRequestListResponse: PullRequestList? = null

    init{
        updatePullRequest()
    }

    fun updatePullRequest() {
        getPullRequest("oppia", "oppia-android", "closed")
    }

    private fun getPullRequest(owner: String, repo: String, pullRequestState: String) = viewModelScope.launch {
        safePullRequestCall(owner,repo,pullRequestState)
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


    private suspend fun safePullRequestCall(owner: String, repo: String, pullRequestState: String){
        pullRequestList.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()) {
                val response = pullRequestRepository.getPullRequest(owner, repo, pullRequestState, pageNumber)
                pullRequestList.postValue(handleResponse(response))
            } else{
                pullRequestList.postValue(Resource.Error("No internet connection"))
            }
        } catch (t:Throwable){
            when(t){
                is IOException -> pullRequestList.postValue(Resource.Error("Network Failure"))
                else -> pullRequestList.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<PullRequestApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?:return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else ->false
                }
            }
        }
        return false
    }
}