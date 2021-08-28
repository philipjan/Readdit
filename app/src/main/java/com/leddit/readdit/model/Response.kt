package com.leddit.readdit.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class RedditResponse<T> {
    data class Loading<T>(val status: T? = null): RedditResponse<T>()
    data class Success<T>(val body: T): RedditResponse<T>()
    data class Failed<T>(val error: Exception): RedditResponse<T>()
    data class Done<T>(val status: T? = null): RedditResponse<T>()
}

@JsonClass(generateAdapter = true)
data class Response(
    @Json(name = "data")
    val data: Data,
    @Json(name = "kind")
    val kind: String
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "after")
    val after: String?,
    @Json(name = "before")
    val before: String?,
    @Json(name = "children")
    val children: MutableList<Children>,
    @Json(name = "dist")
    val dist: Int?,
    @Json(name = "geo_filter")
    val geoFilter: String?,
    @Json(name = "modhash")
    val modhash: String?
)

@JsonClass(generateAdapter = true)
data class Children(
    @Json(name = "data")
    val data: Content,
    @Json(name = "kind")
    val kind: String
)


@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "banner_background_color")
    val bannerBackgroundColor: String?,
    @Json(name = "banner_background_image")
    val bannerBackgroundImage: String?,
    @Json(name = "banner_img")
    val bannerImg: String?,
    @Json(name = "comment_score_hide_mins")
    val commentScoreHideMins: Int?,
    @Json(name = "community_icon")
    val communityIcon: String?,
    @Json(name = "created")
    val created: Double?,
    @Json(name = "created_utc")
    val createdUtc: Double?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "display_name")
    val displayName: String?,
    @Json(name = "display_name_prefixed")
    val displayNamePrefixed: String?,
    @Json(name = "header_img")
    val headerImg: String?,
    @Json(name = "header_title")
    val headerTitle: String?,
    @Json(name = "icon_img")
    val iconImg: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "key_color")
    val keyColor: String?,
    @Json(name = "lang")
    val lang: String?,
    @Json(name = "link_flair_position")
    val linkFlairPosition: String?,
    @Json(name = "mobile_banner_image")
    val mobileBannerImage: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "prediction_leaderboard_entry_type")
    val predictionLeaderboardEntryType: String?,
    @Json(name = "primary_color")
    val primaryColor: String?,
    @Json(name = "public_description")
    val publicDescription: String?,
    @Json(name = "submission_type")
    val submissionType: String?,
    @Json(name = "submit_text")
    val submitText: String?,
    @Json(name = "subreddit_type")
    val subredditType: String?,
    @Json(name = "subscribers")
    val subscribers: Int?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "user_flair_position")
    val userFlairPosition: String?,
    @Json(name = "user_flair_type")
    val userFlairType: String?,
    @Json(name = "videostream_links_count")
    val videostreamLinksCount: Int?,
    @Json(name = "whitelist_status")
    val whitelistStatus: String?
)