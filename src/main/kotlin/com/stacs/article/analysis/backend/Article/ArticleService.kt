package com.stacs.article.analysis.backend.Article

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ArticleService(private val repository: ArticleRepository) {
    fun getAllArticles(): Flux<Article> = repository.findAll()
    fun getArticleByTitle(title: String): Mono<Article> = repository.findByTitle(title)
    fun createArticle(article: Article): Mono<Article> {
        return repository.findByTitle(article.title)
            .flatMap { existingArticle ->
                println("Article already exists: ${existingArticle.title}")
                Mono.just(existingArticle)
            }
            .switchIfEmpty(
                repository.saveInDb(article)
                    .doOnSuccess {
                        println("Article successfully saved: ${article.title}")
                    }
                    .onErrorResume { e ->
                        if (e is DuplicateKeyException) {
                            println("Duplicate key, article already exists: ${article.title}")
                            Mono.empty()
                        } else {
                            Mono.error(e)
                        }
                    }
            )
            .onErrorResume { e ->
                println("Error saving article: ${e.message}")
                Mono.empty()
            }
    }
    fun deleteArticleById(title: String): Mono<Void> = repository.deleteByTitle(title)

    fun linkArticleToProjectUser(articleTitle: String, userName: String): Mono<Article> {
        return repository.findByTitle(articleTitle)
            .flatMap { article ->
                repository.saveUserArticle(userName, articleTitle)
                    .thenReturn(article)
            }
    }

    fun getRedditPostLinksByTitle(articleTitle: String): Flux<String> {
        return repository.findRedditPostLinksByTitle(articleTitle).doOnNext { redditPostLink ->
            println("Gefundener Reddit-Post-Link: $redditPostLink zu Artikel $articleTitle")
        }
    }

    fun getSavedArticlesFromUser(userName: String): Flux<Article> {
        return repository.findSavedArticlesByUser(userName)
    }
}