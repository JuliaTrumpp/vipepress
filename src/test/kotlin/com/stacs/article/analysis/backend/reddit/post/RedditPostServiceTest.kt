package com.stacs.article.analysis.backend.reddit.post

import com.stacs.article.analysis.backend.Reddit.Post.RedditPost
import com.stacs.article.analysis.backend.Reddit.Post.RedditPostRepository
import com.stacs.article.analysis.backend.Reddit.Post.RedditPostService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedditPostServiceTest {

    private lateinit var redditPostRepository: RedditPostRepository
    private lateinit var redditPostService: RedditPostService

    @BeforeEach
    fun setup() {
        redditPostRepository = mockk<RedditPostRepository>()
        redditPostService = RedditPostService(redditPostRepository)
    }

    private fun createARedditPostAndMocks(): RedditPost {
        val redditPost = RedditPost(
            link = "link/to/post",
            title = "Test Title",
            author = "Test Author",
            publishedOn = LocalDate.now(),
            content = "Test Content",
            sentimentScore = 1.0f,
            sentimentMagnitude = 1.0f,
            ups = 12,
            subreddit = "subreddit"
        )

        val savedRedditPost = redditPost.copy(title = "Saved Test Title")

        every { redditPostRepository.saveInDb(redditPost) } returns Mono.just(savedRedditPost)
        every { redditPostRepository.linkArticleToRedditPost(any(), any()) } returns Mono.empty()
        every { redditPostRepository.findByLink(any()) } returns Mono.just(redditPost)

        return redditPost
    }


    @Test
    fun `should save redditPost successfully`() {

        val newRedditPost = createARedditPostAndMocks()

        val monoResult = redditPostService.saveRedditPost(newRedditPost)

        StepVerifier.create(monoResult)
            .assertNext { redditPost ->
                assertEquals("Saved Test Title", redditPost.title)
                assertEquals("link/to/post", redditPost.link)
            }
            .verifyComplete()
    }

    @Test
    fun `should return redditPost by link`() {

        val newRedditPost = createARedditPostAndMocks()

        val monoResult = redditPostService.getRedditPostByLink("link/to/post")

        StepVerifier.create(monoResult)
            .assertNext { redditPost ->
                assertEquals("Test Title", redditPost.title)
                assertEquals("link/to/post", redditPost.link)
            }
            .verifyComplete()
    }
}
