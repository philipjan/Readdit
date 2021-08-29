package com.leddit.readdit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leddit.readdit.adapter.RedditContentAdapter
import com.leddit.readdit.databinding.ActivityMainBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var binder: ActivityMainBinding

    @Inject lateinit var adapter: RedditContentAdapter
    private val mainViewModel: MainActivityViewModel by viewModels()

    companion object {
        var lastStringIndex: String? = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setupSwipeListener()
        setupScrollListener()
        setupAdapter()
        getResponse()

    }

    private fun getResponse(afterString: String? = null) {
        lifecycleScope.launchWhenStarted {
            mainViewModel.getSubredditLists(afterValue = afterString).collect { processResponse(it) }
        }
    }

    private fun processResponse(response: RedditResponse<Response>) {
        when(response) {
            is RedditResponse.Done -> {
                lastStringIndex = null
                setupSwipeLoading(false)
            }
            is RedditResponse.Failed -> {
                lastStringIndex = null
                setupSwipeLoading(false)
            }
            is RedditResponse.Loading -> {
                setupSwipeLoading(true)
            }
            is RedditResponse.Success -> {
                val list = response.body.data.children.map {
                    it.data
                }

                if (lastStringIndex.isNullOrEmpty()) {
                    adapter.submitList(list)
                } else {
                    val currentToBeUpdatedList = adapter.currentList.toMutableList()
                    currentToBeUpdatedList.addAll(list)
                    adapter.submitList(currentToBeUpdatedList)
                }

            }
        }
    }

    private fun setupAdapter() {
        binder.subRedditRecyclerView.run {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupScrollListener() {
        binder.subRedditRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) &&
                        newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastString = adapter.currentList[adapter.currentList.lastIndex].name
                        lastStringIndex = lastString
                        getResponse(lastString)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            }
        )
    }

    private fun setupSwipeListener() {
        binder.swipeRefreshLayout.setOnRefreshListener {
            if (lastStringIndex.isNullOrEmpty()) {
                getResponse()
            }
        }
    }

    private fun setupSwipeLoading(isLoading: Boolean) {
        binder.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }
}