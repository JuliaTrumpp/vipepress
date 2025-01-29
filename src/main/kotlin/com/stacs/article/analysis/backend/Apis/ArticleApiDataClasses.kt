package com.stacs.article.analysis.backend.Apis

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val source: Source?,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,    // todo show picture next to artice on article page
    val publishedAt: String,
    val content: String?
)

data class Source(
    val id: String?,
    val name: String
)
