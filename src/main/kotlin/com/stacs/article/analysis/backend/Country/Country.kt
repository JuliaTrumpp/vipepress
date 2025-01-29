package com.stacs.article.analysis.backend.Country

import com.stacs.article.analysis.backend.Article.Article
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("COUNTRY")
data class Country(
    @Id val short: String,
    val name: String,
    val articles: MutableList<Article>,
    val sources: MutableList<String>,
)
