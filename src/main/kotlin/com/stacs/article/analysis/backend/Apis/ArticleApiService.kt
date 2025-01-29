package com.stacs.article.analysis.backend.Apis

import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Sentiment.SentimentAnalysis
import com.stacs.article.analysis.libarys.UsefullFunctionsLibary
import com.stacs.article.analysis.backend.Article.ArticleService
import com.stacs.article.analysis.backend.Sentiment.Sentiment
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


enum class Category(val value: String) {
    BUSINESS("business"), ENTERTAINMENT("entertainment"), GENERAL("general"), HEALTH("health"), SCIENCE("science"), SPORTS("sports"), TECHNOLOGY("technology")
}

enum class CountryE(val value: String) {
    US("us"), DE("de"), FR("fr")
}

@Service    // todo rename in NewsApiService because similar to ArticleService
class ArticleApiService(val libary: UsefullFunctionsLibary = UsefullFunctionsLibary(), val sentimentAnalysis: SentimentAnalysis = SentimentAnalysis(), val articleService: ArticleService) {

    private val apiKey = System.getenv("NEWS_ARTICLE_API")
    private val webClient: WebClient

    init {
        webClient = WebClient.builder()
            .baseUrl("https://newsapi.org/v2")
            .defaultHeader("User-Agent", "MyNewsApp/1.0")
            .build()
    }

    fun fetchTopHeadlines(
        country: CountryE = CountryE.US,
        category: Category = Category.GENERAL,
        sources: String = "",
        q: String = "",
        howMany: Int = 10
    ): Flux<Article> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/top-headlines")

                uriBuilder.queryParam("country", country.value)
                uriBuilder.queryParam("category", category.value)

                if (sources.isNotBlank()) {
                    uriBuilder.queryParam("sources", sources)
                }
                if (q.isNotBlank()) {
                    uriBuilder.queryParam("q", q)
                }

                uriBuilder.queryParam("apiKey", apiKey)
                uriBuilder.build()
            }
            .retrieve()
            .bodyToMono(NewsApiResponse::class.java)
            .flatMapMany { response -> Flux.fromIterable(response.articles) }
            .flatMap { newsArticle -> mapNewsArticleToArticle(newsArticle) }
            .take(howMany.toLong())
    }

    fun fetchNewsArticles(
        query: String = "",
        qInTitle: String = "",
        sources: String = "",
        from: String = "",
        language: String = "",
        sortBy: String = "",
        howMany: Int = 10
    ): Flux<Article> {
        var noParameter = false

        if(query.isBlank() && qInTitle.isBlank() && sources.isBlank()) { // one of those has to be set (api rules (+domains))
            noParameter = true
        }

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/everything")

                if (query.isNotBlank()) {
                    uriBuilder.queryParam("q", query)
                }
                if (qInTitle.isNotBlank()) {
                    uriBuilder.queryParam("qInTitle", qInTitle)
                }
                if (sources.isNotBlank()) {
                    uriBuilder.queryParam("sources", sources)
                }
                if (from.isNotBlank()) {
                    uriBuilder.queryParam("from", from)
                }
                if (language.isNotBlank()) {
                    uriBuilder.queryParam("language", language)
                }
                if (sortBy.isNotBlank()) {
                    uriBuilder.queryParam("sortBy", sortBy)
                }
                if(noParameter){
                    uriBuilder.queryParam("q", "and")
                }

                uriBuilder.queryParam("apiKey", apiKey)
                uriBuilder.build()
            }
            .retrieve()
            .bodyToMono(NewsApiResponse::class.java)
            .flatMapMany { response -> Flux.fromIterable(response.articles) }
            .flatMap { newsArticle -> mapNewsArticleToArticle(newsArticle) }
            .take(howMany.toLong())
    }


    fun mapNewsArticleToArticle(newsArticle: NewsArticle): Mono<Article> {
        val article = Article(
            title = libary.removeAfterLastDash(newsArticle.title),
            author = newsArticle.author ?: "Not available",
            description = newsArticle.description ?: "Not available",
            publishedOn = libary.parsePublishedDate(newsArticle.publishedAt),
            content = newsArticle.content ?: "Not available",
            source = newsArticle.source?.name ?: "Not available",
            sentimentScore = sentimentAnalysis.analyzeSentiment(newsArticle.content, Sentiment.SCORE),
            sentimentMagnitude = sentimentAnalysis.analyzeSentiment(newsArticle.content, Sentiment.MAGNITUDE)
        )
        return articleService.createArticle(article)
    }
}