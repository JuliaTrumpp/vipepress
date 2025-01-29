package com.stacs.article.analysis.backend.Article

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Table("ARTICLE")
data class Article(
    @Id val title: String,
    val author: String,
    val description: String,
    @Column("published_on") @field:DateTimeFormat(pattern = "yyyy-MM-dd") val publishedOn: LocalDate,
    val content: String,
    val source: String,
    @Column("sentiment_score") val sentimentScore: Float,
    @Column("sentiment_magnitude") val sentimentMagnitude: Float
)
