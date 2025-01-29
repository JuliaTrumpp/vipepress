package com.stacs.article.analysis.Frontend_Controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CountryComparisonPageController {

    @GetMapping("/countries")
    fun getArticlePage(model: Model): Mono<String> {

        return getCountries()
            .collectList()
            .doOnNext { countries ->
                model.addAttribute("sArticles", countries)
            }
            .thenReturn("account")
    }

    private fun getCountries(): Flux<String> {
        // todo returnt liste aus DB
        return Flux.just("1", "2", "3")
    }
}
