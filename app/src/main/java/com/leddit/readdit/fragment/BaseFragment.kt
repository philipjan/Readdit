package com.leddit.readdit.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leddit.readdit.R
import com.leddit.readdit.activity.MainActivity
import com.leddit.readdit.adapter.RedditContentAdapter
import com.leddit.readdit.databinding.FragmentBaseBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class BaseFragment : Fragment() {

    private lateinit var binder: FragmentBaseBinding

    @Inject
    lateinit var adapter: RedditContentAdapter
    private val mainViewModel: MainActivityViewModel by viewModels()

    companion object {
        var lastStringIndex: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentBaseBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUIListeners()
        setupAdapter()
        getResponse()
    }

    private fun setupUIListeners() {
        setupSwipeListener()
        setupScrollListener()
        setFabClickListener()
    }

    private fun getResponse(afterString: String? = null) {
        lifecycleScope.launchWhenStarted {
            mainViewModel.getSubredditLists(
                afterValue = afterString
            ).collect { processResponse(it) }
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
                binder.root.context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = this@BaseFragment.adapter
        }
        adapter.clickListener = {
            val args = bundleOf(Pair(SubredditPostsFragment.KEY_SUBREDDIT_NAME, it.displayName))
            navigateTo(
                R.id.action_baseFragment_to_subredditPostsFragment,
                args
            )
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

                    binder.searchFab.run {
                        when(newState) {
                            RecyclerView.SCROLL_STATE_DRAGGING,
                            RecyclerView.SCROLL_STATE_SETTLING -> {
                                this.isEnabled = false
                                this.animate().alpha(0F)
                            }
                           else -> {
                               this.isEnabled = true
                               this.animate().alpha(1F)
                           }
                        }
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

    private fun setFabClickListener() {
        binder.searchFab.setOnClickListener {
            navigateTo(R.id.action_baseFragment_to_searchFragment)
        }
    }

    private fun navigateTo(id: Int, arg: Bundle? = null) {
        if (arg == null) {
            findNavController().navigate(id)
        } else {
            findNavController().navigate(id, arg)
        }
    }

}