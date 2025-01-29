package com.stacs.article.analysis.Frontend_Controller

import com.stacs.article.analysis.libarys.UsefullFunctionsLibary
import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Reddit.Post.RedditPost
import com.stacs.article.analysis.backend.Apis.RedditApiService
import com.stacs.article.analysis.backend.Article.ArticleService
import com.stacs.article.analysis.backend.Reddit.Post.RedditPostService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Controller
class ArticlePageController(val redditApiService: RedditApiService, val libary: UsefullFunctionsLibary = UsefullFunctionsLibary(), val articleService: ArticleService, val redditPostService: RedditPostService) {

    @GetMapping("/article/{articleTitle}")
    fun getArticlePage(@PathVariable articleTitle: String, model: Model): Mono<String> {
        return getArticle(articleTitle)
            .flatMap { article ->
                getRedditPosts(article)
                    .collectList()
                    .doOnNext { redditPosts ->
                        model.addAttribute("article", article)
                        model.addAttribute("redditPosts", redditPosts)
                        model.addAttribute("redditPostsSentiment", getRedditPostsSentiment(redditPosts))
                        model.addAttribute("date", libary.getCurrentDateString())
                        model.addAttribute("userName", getUserName())
                    }
                    .thenReturn("article")
            }
    }

    fun getUserName(): Mono<String> {
        return Mono.just("Julia")
    }

    fun getRedditPosts(article: Article): Flux<RedditPost> {
//        return articleService.getRedditPostLinksByTitle(article.title).flatMap { redditPostLink ->
//            redditPostService.getRedditPostByLink(redditPostLink)
//        }
//            .switchIfEmpty {
        return redditApiService.fetchPostsAboutArticle(article.title)
//            }
    }

    fun getRedditPostsSentiment(redditPosts: List<RedditPost>): Float {
        val averageScore = redditPosts.map { it.sentimentScore }.average()
        return averageScore.toFloat()
    }

    fun getArticle(articleTitle: String): Mono<Article> {
        return articleService.getArticleByTitle(articleTitle)
    }

    @PostMapping("/saveArticle")
    fun saveArticle(@RequestParam("articleTitle") articleTitle: String, @RequestParam("userName") userName: String, @RequestParam("redditLinks") redditLinks: List<String>): Mono<String> {
        redditLinks.forEach { link ->
            redditPostService.linkArticleToRedditPost(articleTitle, link).subscribe()
        }

        print("Username: $userName")    // todo weil dies irgendein mono ist, erstmal mit default weiter

        return articleService.linkArticleToProjectUser(articleTitle, "Julia")
            .map { "redirect:/article/$articleTitle" }
            .onErrorResume { error ->
                println("Error saving article: ${error.message}")
                Mono.just("redirect:/error")
            }
    }
}