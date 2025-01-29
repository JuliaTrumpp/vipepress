package com.stacs.article.analysis.backend.Reddit.Comment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("REDDIT_COMMENT")
data class RedditComment(
    @Id val link: String,
    val content: String,
    val author: String,
    @Column("published_on") val publishedOn: String,
    val ups: Int,
    @Column("sentiment_score") val sentimentScore: Float,
    @Column("sentiment_magnitude") val sentimentMagnitude: Float
)
