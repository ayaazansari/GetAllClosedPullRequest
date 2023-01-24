package com.example.getallclosedpullrequest.utils

import android.content.Context
import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.getallclosedpullrequest.R
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val QUERY_PAGE_SIZE = 30

        fun getProgressDrawable(context: Context): CircularProgressDrawable {
            return CircularProgressDrawable(context).apply {
                strokeWidth = 10f
                centerRadius = 50f
                start()
            }
        }

        fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
            val options = RequestOptions()
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher_round)

            Glide.with(this.context)
                .setDefaultRequestOptions(options)
                .load(uri)
                .into(this)
        }

        fun formatDate(inputDate: String?): String? {
            return inputDate?.dropLast(10)
        }

    }
}