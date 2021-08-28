package com.leddit.readdit.di

import com.leddit.readdit.adapter.RedditContentAdapter
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
}