package com.stacs.article.analysis.backend.Apis

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.stacs.article.analysis.backend.Sentiment.SentimentAnalysis
import com.stacs.article.analysis.libarys.UsefullFunctionsLibary
import com.stacs.article.analysis.backend.Reddit.Comment.RedditComment
import com.stacs.article.analysis.backend.Reddit.Post.RedditPost
import com.stacs.article.analysis.backend.Reddit.Post.RedditPostService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import com.stacs.article.analysis.backend.Sentiment.Sentiment


@Service
class RedditApiService(val libary: UsefullFunctionsLibary = UsefullFunctionsLibary(), val sentimentAnalysis: SentimentAnalysis = SentimentAnalysis(), val redditPostService: RedditPostService) {
    private lateinit var accessToken: String
    private val webClient: WebClient
    private val webClientForPost: WebClient
    private val clientId: String
    private val clientSecret: String
    private val username: String
    private val password: String

    init {
        // todo these values should not be visibly initialized here
        @Value("\${reddit.access-token}")
        accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNzM4MTAxNTgzLjEwNTYxLCJpYXQiOjE3MzgwMTUxODMuMTA1NjEsImp0aSI6IkxacGNiWWRxTGtoNm5QSVZhS2lldVpxcTZwV1ZudyIsImNpZCI6IlpKXzdoZjlZeHdlbEhkbmxfWUxZRkEiLCJsaWQiOiJ0Ml8xZTh4dWNienZsIiwiYWlkIjoidDJfMWU4eHVjYnp2bCIsImxjYSI6MTczMzIyMjcxODA2NCwic2NwIjoiZUp5S1Z0SlNpZ1VFQUFEX193TnpBU2MiLCJmbG8iOjl9.LxZSZK0tbNDAy9k3BWPsFoxbgi0Y8PNfelxIAUn_1DDSl2tzNXVOAiZTx5ssfB6gTNm2vW_ULnXjbGCQOQlWqEXkRl5-eP_UrOK7PlGZWdB_ip6iDCS_jVQ_17zccb0tcOFdJt2m05zdcbS193sASEkc_PdCvvIYOhZVH7xAnBbwQqpvIy3KYXQQTulPndnAfBpeCUDOuUi3gPq7V7zu5iWhF5ty2GyF1Me0sg-wJlD4lIF-39_DPFkKFK4LUaaWhQkvNMwmYmACkCQ8zlQgkzBhvPBmG5_NFU0zp-5iH30oAWvYVUwE0OQoECuH1akZVG8cpeNRVDztudtuqbI2_w"

        @Value("\${reddit.client-id}")
        clientId = "ZJ_7hf9YxwelHdnl_YLYFA"

        @Value("\${reddit.secret-key}")
        clientSecret = "EoZ5fSiTu3jSqD92NmArrCRotiTkww"

        @Value("\${reddit.username}")
        username = "juliatrumpp_hsrm"

        @Value("\${reddit.password}")
        password = "stacsprojekt"

        val mapper = ObjectMapper().apply {
            configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }.registerKotlinModule()

        val strategies = ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper))
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
            }
            .build()

        webClient = WebClient.builder()
        .baseUrl("https://oauth.reddit.com")
        .exchangeStrategies(strategies)
        .defaultHeader("Authorization", "Bearer $accessToken")
        .defaultHeader("User-Agent", "MyAPI/0.0.1")
        .build()


        // webclient for Post (to fetch new access token)

        val basicAuth = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

        webClientForPost = WebClient.builder()
            .baseUrl("https://www.reddit.com")
            .exchangeStrategies(strategies)
            .defaultHeaders { headers ->
                headers.add("User-Agent", "MyRedditApp/1.0")
                headers.add("Authorization", "Basic $basicAuth")
            }
            .build()
    }

    fun fetchArticleComments(subreddit: String, articleId: String): Flux<RedditComment> {

        val apiUrl = "https://oauth.reddit.com/r/$subreddit/comments/$articleId"
        print("AccessToken before Fetch: ${accessToken}\n")

        val response = webClient.get()
            .uri(apiUrl)
            .headers { headers ->
                headers.setBearerAuth(accessToken)
                headers.add("User-Agent", "MyRedditApp/1.0")
            }
            .retrieve()
            .bodyToFlux(Listing::class.java)
            .doOnError { error ->
                if (error is WebClientResponseException && error.statusCode == HttpStatus.UNAUTHORIZED) {
                    fetchAccessToken()
                    fetchArticleComments(subreddit, articleId)
                } else {
                    Mono.error(error)
                }
            }

        print("AccessToken after Fetch: ${accessToken}")

        return Flux.create<RedditComment> { emitter ->
            var titleProcessed = false

            response.subscribe({ listing ->
                val children = listing.data?.children.orEmpty()

                children.forEach { child ->
                    val childData = child.data
                    if (childData != null) {
                        if (!titleProcessed && child.kind == "t3") {
                            println("Artikel Titel: ${childData.title}")
                            titleProcessed = true
                        } else if (child.kind == "t1") {
                            val redditComment = mapToRedditComment(childData)
                            emitter.next(redditComment)
                        }
                    }
                }
            }, { error ->
                emitter.error(error)
            }, {
                emitter.complete()
            })
        }
    }

    fun fetchPostsAboutArticle(articleTitle: String): Flux<RedditPost> {  // todo new class with articleId, comments/posts; to what extent a specific article or only something similar was found

        val apiUrl = "https://oauth.reddit.com/search?q=$articleTitle"

        val response = webClient.get()
            .uri(apiUrl)
            .headers { headers ->
                headers.setBearerAuth(accessToken)
                headers.add("User-Agent", "MyRedditApp/1.0")
            }
            .retrieve()
            .bodyToMono(Listing::class.java)
            .flatMapMany { listing ->
                val children = listing.data?.children.orEmpty()
                Flux.fromIterable(children)
            }
            .flatMap { child ->
                val childData = child.data
                if (childData != null) {
                    mapToRedditPost(child.data, articleTitle)
                        .flatMapMany { redditPost ->
                            if (libary.textContainsUrl(redditPost.title) && libary.textContainsUrl(redditPost.content)) {
                                Flux.just(redditPost)
                            } else {
                                Flux.empty()
                            }
                        }
                } else {
                    Flux.empty()
                }
            }
            .collectList()
            .flatMapMany { redditPosts ->
                if (redditPosts.size < 3) {
                    val shortenedTitle = articleTitle.substringBefore(".").substringBefore(",")
                    if (shortenedTitle != articleTitle) {
                        fetchPostsAboutArticle(shortenedTitle)
                    } else {
                        Flux.empty()
                    }
                } else {
                    val topPosts = redditPosts
                        .sortedByDescending { it.sentimentMagnitude }
                        .take(3)
                    Flux.fromIterable(topPosts)
                }
            }
            .doOnError { error ->
                if (error is WebClientResponseException && error.statusCode == HttpStatus.UNAUTHORIZED) {
                    fetchAccessToken()
                    fetchPostsAboutArticle(articleTitle)
                } else {
                    Mono.error(error)
                }
            }
        return response
    }

    fun fetchAccessToken(): String? {

        val response =  webClientForPost.post()
            .uri("/api/v1/access_token")
            .bodyValue(
                mapOf(
                    "grant_type" to "password",
                    "username" to username,
                    "password" to password
                )
            )
            .retrieve()
            .bodyToMono(AccessTokenResponse::class.java)
            .switchIfEmpty(Mono.error(IllegalStateException("AccessTokenResponse is null")))
            .map { newToken ->
                if (newToken != null) { accessToken = newToken.accessToken ?: "" }
                println("Neues access token wurde gesetzt ${accessToken}, token type: ${newToken.tokenType}")       // todo both null
                newToken.accessToken
            }
            .doOnError { ex ->
                println("Failed to fetch access token: ${ex.message}")
            }
        return response.toString()
    }

    fun mapToRedditComment(childData: ChildData): RedditComment {       // todo change to Mono<RedditComment>

        return RedditComment(
            link = childData.permalink ?: "Not available",
            content = childData.body ?: "Not available",
            author = childData.author ?: "Not available",
            publishedOn = libary.doubleToIsoString(childData.created, false),
            sentimentScore = sentimentAnalysis.analyzeSentiment(childData.body, Sentiment.SCORE),
            sentimentMagnitude = sentimentAnalysis.analyzeSentiment(childData.body, Sentiment.MAGNITUDE),
            ups = childData.ups ?: 0
        )
    }

    fun mapToRedditPost(childData: ChildData, articleTitle: String): Mono<RedditPost> {

        val redditPost =  RedditPost(
            link = childData.permalink ?: "Not available",
            title = libary.limitStingLength(childData.title),
            content = libary.limitStingLength(childData.selftext),
            author = childData.author ?: "Not available",
            publishedOn =  libary.parseUtcToDate(childData.created),
            sentimentScore = sentimentAnalysis.analyzeSentiment(listOf(childData.title, childData.selftext), Sentiment.SCORE),
            sentimentMagnitude = sentimentAnalysis.analyzeSentiment(listOf(childData.title, childData.selftext), Sentiment.MAGNITUDE),
            ups = childData.ups ?: 0,
            subreddit = childData.subreddit ?: "Not available"
        )

        return redditPostService.saveRedditPost(redditPost)
    }
}