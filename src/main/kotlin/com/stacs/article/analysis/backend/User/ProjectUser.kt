package com.stacs.article.analysis.backend.User

import com.stacs.article.analysis.backend.Article.Article
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("PROJECT_USER")
data class ProjectUser (
    @Id val name: String,
    val savedArticles: MutableList<Article>? = mutableListOf(),
    @Column("sources") val savedSources: MutableList<String> = mutableListOf()
)