package com.stacs.article.analysis.backend.Reddit.Post

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Table("REDDIT_POST")
data class RedditPost (
    @Id val link: String,
    val title: String,
    val content: String,
    val author: String,
    @Column("published_on") @field:DateTimeFormat(pattern = "yyyy-MM-dd") val publishedOn: LocalDate,
    val ups: Int,
    val subreddit: String,
    @Column("sentiment_score") val sentimentScore: Float,
    @Column("sentiment_magnitude") val sentimentMagnitude: Float
)