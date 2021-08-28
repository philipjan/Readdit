package com.leddit.readdit.viewmodel

import androidx.lifecycle.ViewModel
import com.leddit.readdit.Readdit
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.redditclient.RedditClient
import com.leddit.readdit.repository.RedditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repo: RedditRepository
) : ViewModel() {

    fun getSubredditLists() = flow<RedditResponse<Response>> {
        emit(RedditResponse.Loading())
        val response = repo.getSubreddits()
        try {
            emit(RedditResponse.Success(response))
        } catch (e: Exception) {
            emit(RedditResponse.Failed(e))
        } finally {
            emit(RedditResponse.Done())
        }
    }.flowOn(Dispatchers.IO)
}