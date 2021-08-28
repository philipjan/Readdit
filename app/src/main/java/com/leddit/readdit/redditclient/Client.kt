package com.leddit.readdit.redditclient

import android.content.Context
import androidx.core.content.ContextCompat
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import javax.inject.Inject

class RedditClient @Inject constructor(
    val ctx: Context
    ) {

    private val BASE_URL = "https://api.reddit.com"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .cache(Cache(
                directory = File(
                    ContextCompat.getCodeCacheDir(ctx),
                    "readdit_cache"
                ),
                maxSize = 50L * 1024 *1024 // 50mb
            ))
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getRedditApi(): RedditService {
        return getRetrofit().create(RedditService::class.java)
    }
}


interface RedditService {

    @GET("/subreddits.json?limit=100")
    suspend fun getSubReddits(@Query("after") after: String? = null): Response

    @GET("/subreddits/search.json?include_over_18=false")
    suspend fun searchSubReddit(
        @Query("q") queryString: String? = null,
        @Query("after") after: String? = null
    ): Response
}