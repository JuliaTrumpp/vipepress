package com.stacs.article.analysis.Frontend_Controller

import com.stacs.article.analysis.libarys.UsefullFunctionsLibary
import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Article.ArticleService
import com.stacs.article.analysis.backend.User.ProjectUserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Controller
class AccountPageController(private val userService: ProjectUserService, val libary: UsefullFunctionsLibary = UsefullFunctionsLibary(), val articleService: ArticleService) {

    @GetMapping("/account/{userName}")
    fun getArticlePage(@PathVariable userName: String, model: Model): Mono<String> {

        return Mono.zip(
            getSavedArticles(userName).collectList(),
            getSavedSources(userName)
        )
            .doOnNext { result ->
                val articles = result.t1
                val sources = result.t2

                model.addAttribute("userName", userName)
                model.addAttribute("savedArticles", articles)
                model.addAttribute("savedSources", sources)
                model.addAttribute("date", libary.getCurrentDateString())
            }
            .thenReturn("account")
    }

    private fun getSavedArticles(userName: String): Flux<Article> {
        return articleService.getSavedArticlesFromUser(userName)
    }

    private fun getSavedSources(userName: String): Mono<List<String>> {
        return userService.getSourcesFromUserByName(userName)
    }
}