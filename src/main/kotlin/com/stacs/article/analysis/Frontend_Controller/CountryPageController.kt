package com.stacs.article.analysis.Frontend_Controller

import com.stacs.article.analysis.backend.Apis.ArticleApiService
import com.stacs.article.analysis.backend.Apis.Category
import com.stacs.article.analysis.backend.Apis.CountryE
import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Country.Country
import com.stacs.article.analysis.backend.Country.CountryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Controller
class CountryPageController(val articleApiService: ArticleApiService, val countryService: CountryService) {

    @GetMapping("/country{countryShort}")
    fun getArticlePage(@PathVariable countryShort: String, model: Model): Mono<String> {
        return getCountry(countryShort)
            .flatMap { country ->
                Mono.justOrEmpty(mapToCountryEnum(country.short))
                    .switchIfEmpty(Mono.empty())
                    .flatMap { countryEnum ->
                        getCountryArticles(countryEnum)
                            .collectList()
                            .doOnNext { articles ->
                                model.addAttribute("countryName", country.name)
                                model.addAttribute("savedArticles", articles)
                            }
                            .thenReturn("country")
                    }
            }
    }


    private fun getCountryArticles(country: CountryE): Flux<Article> {
        return articleApiService.fetchTopHeadlines(country = country)
    }

    private fun getCountry(short: String): Mono<Country> {
        return countryService.getCountryByShort(short)
    }

    private fun mapToCountryEnum(short: String): CountryE? {
        return when (short.lowercase()) {
            CountryE.US.value -> CountryE.US
            CountryE.DE.value -> CountryE.DE
            CountryE.FR.value -> CountryE.FR
            else -> null
        }
    }
}
