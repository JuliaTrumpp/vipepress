package com.stacs.article.analysis.backend.Article

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ArticleRepository : ReactiveCrudRepository<Article, String> {

    fun findByTitle(title: String): Mono<Article>

    @Query("""
        INSERT INTO ARTICLE (title, author, description, published_on, content, source, sentiment_score, sentiment_magnitude)
        VALUES (:#{#article.title}, :#{#article.author}, :#{#article.description}, :#{#article.publishedOn}, 
                :#{#article.content}, :#{#article.source}, :#{#article.sentimentScore}, :#{#article.sentimentMagnitude})
    """)
    fun saveInDb(@Param("article") article: Article): Mono<Article>
    fun deleteByTitle(title: String): Mono<Void>

    @Query("INSERT INTO USER_SAVED_ARTICLE (user_name, article_title) VALUES (:userName, :articleTitle)")
    fun saveUserArticle(@Param("userName") userName: String, @Param("articleTitle") articleTitle: String): Mono<Void>

    @Query("SELECT reddit_post_link FROM ARTICLE_REDDIT_POST WHERE article_title = :articleTitle")
    fun findRedditPostLinksByTitle(@Param("articleTitle") articleTitle: String): Flux<String>

    @Query("""
        SELECT a.*
        FROM PROJECT_USER pu
        JOIN USER_SAVED_ARTICLE usa ON pu.name = usa.user_name
        JOIN ARTICLE a ON usa.article_title = a.title
        WHERE pu.name =:userName;
    """)
    fun findSavedArticlesByUser(@Param("userName") userName: String): Flux<Article>     // todo das muss in den anderen service
}

