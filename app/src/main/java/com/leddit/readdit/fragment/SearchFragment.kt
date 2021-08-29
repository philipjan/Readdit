package com.leddit.readdit.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leddit.readdit.R
import com.leddit.readdit.adapter.RedditContentAdapter
import com.leddit.readdit.databinding.FragmentSearchBinding
import com.leddit.readdit.model.RedditResponse
import com.leddit.readdit.model.Response
import com.leddit.readdit.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private var queryString = ""
    private lateinit var binder: FragmentSearchBinding
    @Inject
    lateinit var adapter: RedditContentAdapter
    private val viewModel: MainActivityViewModel by viewModels()


    companion object {
       var lastStringIndex: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentSearchBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binder.swipeRefreshLayout.setOnRefreshListener {
            if (lastStringIndex.isNullOrEmpty()) {
                searchItem(
                    query = queryString,
                    after = lastStringIndex
                )
            } else {
                searchItem(
                    query = queryString,
                    after = null
                )
            }
        }
    }

    private fun setSwipeRefreshLoading(isLoading: Boolean) {
        binder.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun setupRecyclerView() {
        binder.recycler.run {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            adapter = this@SearchFragment.adapter

            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1) &&
                            newState == RecyclerView.SCROLL_STATE_IDLE) {
                                if (!this@SearchFragment.adapter.currentList.isNullOrEmpty()) {
                                    val lastStringIdx = this@SearchFragment
                                        .adapter
                                        .currentList[
                                            this@SearchFragment.adapter.currentList.lastIndex
                                    ].name
                                    lastStringIndex = lastStringIdx
                                    searchItem(
                                        query = queryString,
                                        after = lastStringIdx
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
            val args = bundleOf(Pair(SubredditPostsFragment.KEY_SUBREDDIT_NAME, it.displayName))
            navigateTo(
                R.id.action_searchFragment_to_subredditPostsFragment,
                args
            )
        }

    }

    private fun setupToolbar() {
        binder.toolbar.run {
            setNavigationIconTint(ContextCompat.getColor(binder.root.context, R.color.white))
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        binder.searchView.addTextChangedListener {
            val searched = it.toString().trim()
            if (searched == queryString) return@addTextChangedListener
            queryString = searched

            lifecycleScope.launch {
              delay(300)
              if (searched != queryString) return@launch
                searchItem(queryString)
            }
        }
    }

    private fun searchItem(query: String, after: String? = null) {
       viewLifecycleOwner.lifecycleScope.launchWhenStarted {
           viewModel.searchSubReddit(
               afterValue = after,
               queryValue = query
           ).collect {
              processData(it)
           }
       }
    }

    private fun processData(response: RedditResponse<Response>) {
        when(response) {
            is RedditResponse.Loading-> {
                setSwipeRefreshLoading(true)
            }
            is RedditResponse.Done -> {
                setSwipeRefreshLoading(false)
                lastStringIndex = null
            }
            is RedditResponse.Failed -> {
                handleError(response.error)
            }
            is RedditResponse.Success -> {
                val data = response.body.data.children.map {
                    it.data
                }

                if (lastStringIndex.isNullOrEmpty()) {
                    adapter.submitList(data)
                } else {
                    val currentLists = adapter.currentList.toMutableList()
                    currentLists.addAll(data)
                    adapter.submitList(currentLists)
                }

            }
        }
    }

    private fun showLogs(msg: String) {
        Log.d(TAG, msg)
    }

    private fun navigateTo(id: Int, arg: Bundle? = null) {
        if (arg == null) {
            findNavController().navigate(id)
        } else {
            findNavController().navigate(id, arg)
        }
    }

    private fun handleError(e: Exception) {
        when(e) {
            is UnknownHostException -> {
                showToast("Please check your internet connection and try again")
            }
            is HttpException -> {
                when(e.code()) {
                    404,
                    500,
                    501,
                    502,
                    503-> {
                        showToast("A server error occured. Please try again later.")
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}