package com.leddit.readdit.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leddit.readdit.R
import com.leddit.readdit.adapter.SubRedditPostsAdapter
import com.leddit.readdit.databinding.FragmentSubredditPostsBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.SubredditPostsResponse
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject


@AndroidEntryPoint
class SubredditPostsFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var binder: FragmentSubredditPostsBinding
    @Inject lateinit var adapter: SubRedditPostsAdapter
    private val viewModel: MainActivityViewModel by viewModels()

    companion object {
        const val KEY_SUBREDDIT_NAME = "subredditname"
        const val VALUE_DEFAULT_PAGE = "home"
        const val VALUE_DEFAULT_PAGE_REDDIT_URL = "https://www.reddit.com"
        const val VALUE_DEFAULT_PAGE_URL = "$VALUE_DEFAULT_PAGE_REDDIT_URL/r/$VALUE_DEFAULT_PAGE"
        var lastStringIndex: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentSubredditPostsBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.containsKey(KEY_SUBREDDIT_NAME) == true) {
            getSubRedditPosts(arguments?.getString(KEY_SUBREDDIT_NAME) ?: VALUE_DEFAULT_PAGE)
        }
        setupUi()
    }

    private fun setupUi() {
        setupRecyclerView()
        setupSwipeRefresh()
    }

    fun getArgs(): String {
        return arguments?.getString(KEY_SUBREDDIT_NAME) ?: VALUE_DEFAULT_PAGE
    }

    private fun setupSwipeRefresh() {
        binder.swipeRefreshLayout.setOnRefreshListener {
            if (lastStringIndex.isNullOrEmpty()) {
                getSubRedditPosts(
                    subReddit = getArgs()
                )
            } else {
                getSubRedditPosts(
                    subReddit = getArgs(),
                    afterIndex = lastStringIndex
                )
            }
        }
    }

    private fun setSwipeRefresh(isLoading: Boolean) {
        binder.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun setupRecyclerView() {
        binder.subRedditPostsRecyclerView.run {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )

            adapter = this@SubredditPostsFragment.adapter

            this.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1) &&
                            newState == RecyclerView.SCROLL_STATE_IDLE) {
                                if (!this@SubredditPostsFragment.adapter.currentList.isNullOrEmpty()) {
                                    val lastString = this@SubredditPostsFragment
                                        .adapter
                                        .currentList[
                                            this@SubredditPostsFragment.adapter.currentList.lastIndex
                                    ].name
                                    lastStringIndex = lastString
                                    getSubRedditPosts(
                                        subReddit = getArgs(),
                                        afterIndex = lastStringIndex
                                    )
                                }
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                    }
                }
            )
        }

        adapter.clickListener = {
            launchSubPost(it.permalink)
        }
    }

    private fun getSubRedditPosts(
        subReddit: String,
        afterIndex: String? = null
    ) {
        lifecycleScope.launchWhenStarted {
            viewModel.getSubRedditPosts(
                afterIndex = afterIndex,
                subReddit = subReddit
            ).collect {
                processResults(it)
            }
        }
    }

    private fun processResults(data: RedditResponse<SubredditPostsResponse>) {
        when(data) {
            is RedditResponse.Loading -> {
                setSwipeRefresh(true)
            }
            is RedditResponse.Done -> {
                lastStringIndex = null
                setSwipeRefresh(false)
            }
            is RedditResponse.Failed -> {
               handleError(data.error)
            }
            is RedditResponse.Success -> {
                val filteredData = data.body.data?.children?.map {
                    it?.data
                }
                if (!lastStringIndex.isNullOrEmpty()) {
                    val currentLists = adapter.currentList.toMutableList()
                    currentLists.addAll(filteredData ?: emptyList())
                    adapter.submitList(currentLists)
                } else {
                    adapter.submitList(filteredData)
                }
            }
        }
    }

    private fun launchSubPost(url: String?) {
        val i  = Intent(Intent.ACTION_VIEW)

        if (url.isNullOrEmpty()) {
            i.data = Uri.parse(VALUE_DEFAULT_PAGE_URL)
        } else {
            i.data = Uri.parse("$VALUE_DEFAULT_PAGE_REDDIT_URL$url")
        }
        startActivity(i)
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }

    private fun handleError(e: Exception) {
        when(e) {
            is UnknownHostException -> {
                showToast(getString(R.string.error_msg_no_internet))
            }
            is HttpException -> {
                when(e.code()) {
                    404,
                    500,
                    501,
                    502,
                    503-> {
                        showToast(getString(R.string.error_msg_server))
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}