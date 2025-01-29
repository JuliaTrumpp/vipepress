package com.stacs.article.analysis.backend.Apis

import com.stacs.article.analysis.Sentiment.*
import com.stacs.article.analysis.backend.Article.Article
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * Service Class to use the get Sentiment from the Natural Language API
 * API Key must be provided as a environment variable ($GOOGLE_CLOUD_API)
 */
@Service
class SentimentApiService {
    private lateinit var apiKey: String
    private val webClient: WebClient

    init {
        apiKey = System.getenv("GOOGLE_CLOUD_API")
        webClient = WebClient.builder()
            .baseUrl("https://language.googleapis.com/v2")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json", "charset=utf-8")
            .build()
    }

    /**
     * retrieves Sentiment for article title and content
     *
     * @return SentimentResponse contains sentiment for each sentence in content and for the whole article
     */
    fun getArticleSentiment(article: Article): Mono<SentimentResponse> {
        val requestBody = SentimentRequest(
            encodingType = "UTF8",
            document = Document(
                type = "PLAIN_TEXT",
                content = """
                    ${article.title}
                    
                    ${article.content}
                """.trimIndent()
            )
        )

        val response = webClient.post()
            .uri("/documents:analyzeSentiment?key=${apiKey}")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(SentimentResponse::class.java)
            .doOnError {println(it.message)}

        return response
    }

    /**
     * To test other stuff, while Google Cloud API not working
     * @return mock Mono<SentimentResponse>
     */
    fun mockGetArticleSentiment(article: Article): Mono<SentimentResponse> {
        var response: SentimentResponse = SentimentResponse(
            language = "en",
            sentences = listOf(
                Sentence(
                    text = Text(content = "testContent", beginOffset = 0),
                    sentiment = Sentiment(magnitude = 0.4, score = 0.2)
                ),
                Sentence(
                    text = Text(content = "zweiter Satz", beginOffset = 0),
                    sentiment = Sentiment(magnitude = 0.8, score = 0.97)
                ),
            ),
            documentSentiment = DocumentSentiment(
                magnitude = 0.6,
                score = 0.58
            )
        )
        return Mono.just(response)
    }
}