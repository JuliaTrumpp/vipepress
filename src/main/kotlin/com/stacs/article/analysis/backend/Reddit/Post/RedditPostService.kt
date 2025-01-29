package com.stacs.article.analysis.backend.Reddit.Post

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RedditPostService(private val repository: RedditPostRepository) {
    fun saveRedditPost(redditPost: RedditPost): Mono<RedditPost> {
        return repository.saveInDb(redditPost)
            .doOnSuccess {
                println("Post successfully saved: ${redditPost.title}")
            }
            .onErrorResume(DuplicateKeyException::class.java) { ex ->
                println("Duplicate key error when saving Reddit post: ${ex.message}")
                Mono.just(redditPost)
            }
    }

    fun linkArticleToRedditPost(articleTitle: String, redditPostLink: String): Mono<Void> {
        return repository.linkArticleToRedditPost(articleTitle, redditPostLink)
            .doOnSuccess {
                println("Article successfully linked to post: ${redditPostLink}, ${articleTitle}")
            }
            .onErrorResume(DuplicateKeyException::class.java) { ex ->
                println("Duplicate key error when linking article to Reddit post: ${ex.message}")
                Mono.empty()
            }
    }

    fun getRedditPostByLink(link: String): Mono<RedditPost> {
        return repository.findByLink(link)
    }
}