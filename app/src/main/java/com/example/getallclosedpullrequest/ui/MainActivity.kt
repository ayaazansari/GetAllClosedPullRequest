package com.example.getallclosedpullrequest.ui

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getallclosedpullrequest.R
import com.example.getallclosedpullrequest.adapters.PullRequestAdapter
import com.example.getallclosedpullrequest.repository.PullRequestRepository
import com.example.getallclosedpullrequest.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.getallclosedpullrequest.utils.Resource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: PullRequestViewModel
    lateinit var pullRequestAdapter: PullRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pullRequestRepository = PullRequestRepository()
        val viewModelProviderFactory = PullRequestViewModelProviderFactory(application,pullRequestRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(PullRequestViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.pullRequestList.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    if (response.data == null) {
                        listError.visibility = View.VISIBLE
                        hideProgressBar()
                    } else {
                        response.data?.let { pullRequestResponse ->
                            recyclerList.visibility = View.VISIBLE
                            pullRequestAdapter.updatePullRequestList(pullRequestResponse.toList())
                            val totalPages = pullRequestResponse.size / QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.pageNumber == totalPages
                        }
                    }
                }
                is Resource.Error -> {
                    listError.visibility = View.VISIBLE
                    recyclerList.visibility = View.GONE
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(this,"An error occured: $message",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                else -> {
                    hideProgressBar()
                }
            }

        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = false
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.updatePullRequest()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        pullRequestAdapter = PullRequestAdapter(arrayListOf())
        recyclerList.apply {
            adapter = pullRequestAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(this@MainActivity.scrollListener)
        }
    }
}