package com.leddit.readdit.di

import android.content.Context
import com.leddit.readdit.redditclient.RedditClient
import com.leddit.readdit.redditclient.RedditService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RedditServiceModule {

    @Provides
    fun provideRetrofit(@ApplicationContext ctx: Context): RedditClient {
        return RedditClient(ctx)
    }

    @Provides
    fun provideRedditService(client: RedditClient): RedditService {
        return client.getRedditApi()
    }
}