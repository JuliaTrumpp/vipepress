package com.stacs.article.analysis.backend.Reddit.Post

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RedditPostRepository : ReactiveCrudRepository<RedditPost, String> {

    @Query("INSERT INTO ARTICLE_REDDIT_POST (article_title, reddit_post_link) VALUES (:articleTitle, :redditPostLink)")
    fun linkArticleToRedditPost(
        @Param("articleTitle") articleTitle: String,
        @Param("redditPostLink") redditPostLink: String
    ): Mono<Void>

    @Query("""
    INSERT INTO REDDIT_POST (
        link, title, content, author, published_on, ups, subreddit, sentiment_score, sentiment_magnitude
    ) VALUES (
        :#{#redditPost.link}, 
        :#{#redditPost.title}, 
        :#{#redditPost.content}, 
        :#{#redditPost.author}, 
        :#{#redditPost.publishedOn}, 
        :#{#redditPost.ups}, 
        :#{#redditPost.subreddit}, 
        :#{#redditPost.sentimentScore}, 
        :#{#redditPost.sentimentMagnitude}
    )
    RETURNING *
    """)
    fun saveInDb(redditPost: RedditPost): Mono<RedditPost>

    fun findByLink(link: String): Mono<RedditPost>

}