package com.leddit.readdit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.leddit.readdit.databinding.TestMainBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var binder: TestMainBinding
    private val mainViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = TestMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
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
            }
        }
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }
}