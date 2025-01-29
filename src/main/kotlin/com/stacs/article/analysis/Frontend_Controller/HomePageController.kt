package com.stacs.article.analysis.Frontend_Controller
import com.stacs.article.analysis.libarys.UsefullFunctionsLibary
import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Apis.ArticleApiService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class HomePageController(val articleApiService: ArticleApiService , val libary: UsefullFunctionsLibary = UsefullFunctionsLibary()
) {

    @GetMapping("")
    fun getHomePage(model: Model): Mono<String> {

        return getTopHeadlines()
            .collectList()
            .doOnNext { articles ->
                model.addAttribute("articles", articles)
                model.addAttribute("date", libary.getCurrentDateString())
            }
            .thenReturn("homepage")
    }

    fun getTopHeadlines(): Flux<Article> {
        return articleApiService.fetchTopHeadlines()
    }

    @GetMapping("/search")
    fun searchArticles(
        @RequestParam query: String,
        model: Model
    ): Mono<String> {
        return Mono.zip(
            articleApiService.fetchNewsArticles(qInTitle = query).collectList(),
            articleApiService.fetchTopHeadlines().collectList()
        )
            .doOnNext { result ->
                val searchedArticles = result.t1
                val articles = result.t2

                model.addAttribute("searchedArticles", searchedArticles)
                model.addAttribute("query", query)
                model.addAttribute("articles", articles)
                model.addAttribute("date", libary.getCurrentDateString())
            }
            .thenReturn("homepage")
    }
}