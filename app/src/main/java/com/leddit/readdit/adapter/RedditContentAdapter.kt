package com.leddit.readdit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.leddit.readdit.R
import com.leddit.readdit.databinding.LiRedditContentBinding
import com.leddit.readdit.model.Content

class RedditContentAdapter() : ListAdapter<
        Content,
        RedditContentAdapter.RedditContentViewHolder>
    (ContentDiffUtilCallback()) {
    private val TAG = javaClass.simpleName

    inner class RedditContentViewHolder(val binder: LiRedditContentBinding): RecyclerView.ViewHolder(binder.root) {
        fun bind(item: Content) {

            if (!item.bannerBackgroundImage.isNullOrEmpty()) {
                val updatedUrl = item.bannerBackgroundImage.substring(0, item.bannerBackgroundImage.indexOf(
                    "?width",
                    0
                ))
                binder.wideBanner.load(updatedUrl)
            }

            if (!item.communityIcon.isNullOrEmpty()) {
                val updatedUrl = item.communityIcon.substring(0, item.communityIcon.indexOf(
                    "?width",
                    0
                ))
                binder.smallIcon.load(updatedUrl)
            }

            binder.subRedditName.text = item.displayName
            binder.subRedditOtherName.text = item.displayNamePrefixed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditContentViewHolder {
        return RedditContentViewHolder(
            LiRedditContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RedditContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }

}

class ContentDiffUtilCallback() : DiffUtil.ItemCallback<Content>() {
    override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
        return oldItem == newItem
    }

}