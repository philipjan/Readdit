package com.leddit.readdit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.leddit.readdit.databinding.TestMainBinding
import com.leddit.readdit.model.RedditResponse
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
//        setContentView(R.layout.test_main)
        setContentView(binder.root)
        getResponse()
    }

    private fun getResponse() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.getSubredditLists().collect {
                when(it) {
                    is RedditResponse.Done -> {
                        showLogs("Request Done")
                    }
                    is RedditResponse.Failed -> {
                        showLogs("Failed! ${it.error}")
                    }
                    is RedditResponse.Loading -> {
                        showLogs("Loading...")
                    }
                    is RedditResponse.Success -> {
                        showLogs("Success: Size: ${it.body.data.children.size}")
                    }
                }
            }
        }
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }
}