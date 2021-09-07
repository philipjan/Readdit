package com.leddit.readdit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.leddit.readdit.R
import com.leddit.readdit.databinding.LiSubredditPostsBinding
import com.leddit.readdit.model.SubRedditPostContent

class SubRedditPostsAdapter() : ListAdapter<
        SubRedditPostContent,
        SubRedditPostsAdapter.SubRedditPostViewHolder>(SubRedditPostDiffUtilCallback()) {

    var clickListener: (content: SubRedditPostContent) -> Unit = {_ ->}

    inner class SubRedditPostViewHolder(
        val binder: LiSubredditPostsBinding
        ): RecyclerView.ViewHolder(binder.root) {

            fun bind(value: SubRedditPostContent) {
                binder.author.text = "r/${value.author}"
                binder.description.text = value.title

                binder.postBanner.run {
                    if (!value.urlOverriddenByDest.isNullOrEmpty()) {
                        this.scaleType = ImageView.ScaleType.CENTER_CROP
                        this.load(value.urlOverriddenByDest)
                    } else {
                        this.scaleType = ImageView.ScaleType.FIT_CENTER
                        this.load(R.drawable.ic_noimage_placeholder3)
                    }
                }

                binder.root.setOnClickListener {
                    clickListener.invoke(value)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubRedditPostViewHolder {
        return SubRedditPostViewHolder(
           LiSubredditPostsBinding.inflate( LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: SubRedditPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SubRedditPostDiffUtilCallback() : DiffUtil.ItemCallback<SubRedditPostContent>() {
    override fun areItemsTheSame(oldItem: SubRedditPostContent, newItem: SubRedditPostContent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SubRedditPostContent, newItem: SubRedditPostContent): Boolean {
        return oldItem == newItem
    }

}