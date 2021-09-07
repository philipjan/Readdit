package com.leddit.readdit.di

import com.leddit.readdit.adapter.RedditContentAdapter
import com.leddit.readdit.adapter.SubRedditPostsAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object AdapterModule {

    @Provides
    fun provideAdapter(): RedditContentAdapter {
        return RedditContentAdapter()
    }

    @Provides
    fun provideSubRedditPostsAdaper(): SubRedditPostsAdapter {
        return SubRedditPostsAdapter()
    }
}