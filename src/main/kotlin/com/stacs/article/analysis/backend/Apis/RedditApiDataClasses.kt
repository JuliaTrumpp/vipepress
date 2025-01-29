package com.stacs.article.analysis.backend.Apis

data class Listing(
    val kind: String?,
    val data: ListingData?
)

data class ListingData(
    val children: List<Child>?,
)

data class Child(
    val kind: String?,  // t1 or t3
    val data: ChildData?
)

data class ChildData(
    val subreddit: String?,
    val user_reports: List<Any>?,
    val selftext: String?,
    val title: String?,
    val permalink: String?,
    val ups: Int?,
    val author: String?,
    val score: Int?,
    val created: Double?,
    val subreddit_id: String?,
    val mod_reports: List<Any>?,
    val replies: ListingData?,
    val body: String?
)

data class AccessTokenResponse(
    val accessToken: String?,
    val tokenType: String?,
    val expiresIn: Int?,
    val scope: String?
)