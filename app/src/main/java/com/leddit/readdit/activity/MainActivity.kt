package com.leddit.readdit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leddit.readdit.adapter.RedditContentAdapter
import com.leddit.readdit.databinding.TestMainBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var binder: TestMainBinding

    @Inject lateinit var adapter: RedditContentAdapter
    private val mainViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = TestMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        setupAdapter()
        getResponse()

    }

    private fun getResponse() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.getSubredditLists().collect { processResponse(it) }
        }
    }

    private fun processResponse(response: RedditResponse<Response>) {
        when(response) {
            is RedditResponse.Done -> {
                showLogs("Request Done")
            }
            is RedditResponse.Failed -> {
                showLogs("Failed! ${response.error}")
            }
            is RedditResponse.Loading -> {
                showLogs("Loading...")
            }
            is RedditResponse.Success -> {
                showLogs("Success: Size: ${response.body.data.children.size}")
                val list = response.body.data.children.map {
                    it.data
                }
                adapter.submitList(list)
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

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }
}