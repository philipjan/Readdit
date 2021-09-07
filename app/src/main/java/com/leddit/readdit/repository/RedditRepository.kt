package com.leddit.readdit.repository

import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.model.SubredditPostsResponse
import com.leddit.readdit.redditclient.RedditService
import javax.inject.Inject

class RedditRepository @Inject constructor(
    val redditApi: RedditService
    ) {

    suspend fun getSubreddits(after: String? = null): Response {
        return redditApi.getSubReddits(after)
    }
    suspend fun searchSubreddits(
        after: String? = null,
        queryString: String?
    ): Response {
        return redditApi.searchSubReddit(
            queryString = queryString,
            after = after
        )
    }

    suspend fun getSubRedditPosts(
        subReddit: String,
        afterIndex: String? = null
    ): SubredditPostsResponse {
        return redditApi.getSubredditPosts(
            subReddit = subReddit,
            after = afterIndex
        )
    }
}