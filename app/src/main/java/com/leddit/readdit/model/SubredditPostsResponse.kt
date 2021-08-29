package com.leddit.readdit.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubredditPostsResponse(
    @Json(name = "data")
    val data: SubRedditPostData?,
    @Json(name = "kind")
    val kind: String?
)

@JsonClass(generateAdapter = true)
data class SubRedditPostData(
    @Json(name = "after")
    val after: String?,
    @Json(name = "before")
    val before: Any?,
    @Json(name = "children")
    val children: List<SubRedditPostChildren?>?,
    @Json(name = "dist")
    val dist: Int?,
    @Json(name = "geo_filter")
    val geoFilter: Any?,
    @Json(name = "modhash")
    val modhash: String?
)

@JsonClass(generateAdapter = true)
data class SubRedditPostChildren(
    @Json(name = "data")
    val data: SubRedditPostContent?,
    @Json(name = "kind")
    val kind: String?
)

@JsonClass(generateAdapter = true)
data class SubRedditPostContent(
    @Json(name = "allow_live_comments")
    val allowLiveComments: Boolean?,
    @Json(name = "archived")
    val archived: Boolean?,
    @Json(name = "author")
    val author: String?,
    @Json(name = "author_fullname")
    val authorFullname: String?,
    @Json(name = "created")
    val created: Double?,
    @Json(name = "created_utc")
    val createdUtc: Double?,
    @Json(name = "domain")
    val domain: String?,
    @Json(name = "downs")
    val downs: Int?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "no_follow")
    val noFollow: Boolean?,
    @Json(name = "num_comments")
    val numComments: Int?,
    @Json(name = "num_crossposts")
    val numCrossposts: Int?,
    @Json(name = "over_18")
    val over18: Boolean?,
    @Json(name = "parent_whitelist_status")
    val parentWhitelistStatus: String?,
    @Json(name = "permalink")
    val permalink: String?,
    @Json(name = "post_hint")
    val postHint: String?,
    @Json(name = "pwls")
    val pwls: Int?,
    @Json(name = "quarantine")
    val quarantine: Boolean?,
    @Json(name = "saved")
    val saved: Boolean?,
    @Json(name = "score")
    val score: Int?,
    @Json(name = "selftext")
    val selftext: String?,
    @Json(name = "selftext_html")
    val selftextHtml: String?,
    @Json(name = "send_replies")
    val sendReplies: Boolean?,
    @Json(name = "spoiler")
    val spoiler: Boolean?,
    @Json(name = "stickied")
    val stickied: Boolean?,
    @Json(name = "subreddit")
    val subreddit: String?,
    @Json(name = "subreddit_id")
    val subredditId: String?,
    @Json(name = "subreddit_name_prefixed")
    val subredditNamePrefixed: String?,
    @Json(name = "subreddit_subscribers")
    val subredditSubscribers: Int?,
    @Json(name = "subreddit_type")
    val subredditType: String?,
    @Json(name = "suggested_sort")
    val suggestedSort: String?,
    @Json(name = "thumbnail")
    val thumbnail: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "total_awards_received")
    val totalAwardsReceived: Int?,
    @Json(name = "ups")
    val ups: Int?,
    @Json(name = "upvote_ratio")
    val upvoteRatio: Double?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "url_overridden_by_dest")
    val urlOverriddenByDest: String?,
    @Json(name = "view_count")
    val viewCount: Any?,
    @Json(name = "visited")
    val visited: Boolean?
)