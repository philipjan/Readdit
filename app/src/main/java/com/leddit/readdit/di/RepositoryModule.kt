package com.leddit.readdit.di

import com.leddit.readdit.redditclient.RedditService
import com.leddit.readdit.repository.RedditRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRedditRepository(service: RedditService): RedditRepository {
        return RedditRepository(service)
    }
}