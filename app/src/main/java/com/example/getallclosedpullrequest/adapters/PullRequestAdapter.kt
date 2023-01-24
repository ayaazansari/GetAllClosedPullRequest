package com.example.getallclosedpullrequest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.getallclosedpullrequest.R
import com.example.getallclosedpullrequest.model.PullRequest
import com.example.getallclosedpullrequest.utils.Constants.Companion.formatDate
import com.example.getallclosedpullrequest.utils.Constants.Companion.getProgressDrawable
import com.example.getallclosedpullrequest.utils.Constants.Companion.loadImage
import kotlinx.android.synthetic.main.item_list.view.*

class PullRequestAdapter(var pullRequest: ArrayList<PullRequest>) :
    RecyclerView.Adapter<PullRequestAdapter.PullListRequestViewHolder>() {

    fun updatePullRequestList(newPullRequest: List<PullRequest>) {
        pullRequest.clear()
        pullRequest.addAll(newPullRequest)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PullListRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return PullListRequestViewHolder(view)
    }

    override fun getItemCount():Int {
        return pullRequest.size
    }

    override fun onBindViewHolder(holder: PullListRequestViewHolder, position: Int) {
        holder.bind(pullRequest[position])
    }

    class PullListRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.title
        private val createdDate = view.created_date
        private val closedDate = view.closed_date
        private val imageView = view.imageView
        private val username = view.user
        private val progressDrawable = getProgressDrawable(view.context)


        fun bind(pullRequest: PullRequest) {
            title.text = pullRequest.title
            createdDate.text = formatDate(pullRequest.createdDate)
            closedDate.text = formatDate(pullRequest.closedDate)
            username.text = pullRequest.user?.name
            imageView.loadImage(pullRequest.user?.profileUrl, progressDrawable)
        }
    }
}