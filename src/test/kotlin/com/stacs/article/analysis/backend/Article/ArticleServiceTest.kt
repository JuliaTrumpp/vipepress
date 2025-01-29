import com.stacs.article.analysis.backend.Article.Article
import com.stacs.article.analysis.backend.Article.ArticleRepository
import com.stacs.article.analysis.backend.Article.ArticleService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleServiceTest {

    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleService: ArticleService

    @BeforeEach
    fun setup() {
        articleRepository = mockk<ArticleRepository>()
        articleService = ArticleService(articleRepository)
    }

    private fun createArtickleAndMocks(): Article {
        val newArticle = Article(
            title = "Test Title",
            author = "Test Author",
            description = "Test Description",
            publishedOn = LocalDate.now(),
            content = "Test Content",
            source = "Test Source",
            sentimentScore = 1.0f,
            sentimentMagnitude = 1.0f
        )

        val savedArticle = newArticle.copy(title = "Saved Test Title")

        every { articleRepository.saveInDb(newArticle) } returns Mono.just(savedArticle)
        every { articleRepository.findByTitle(any()) } returns Mono.empty()
        every { articleRepository.findRedditPostLinksByTitle(any()) } returns Flux.just<String>("link/to/title/1", "link/to/title/2")

        return newArticle
    }


    @Test
    fun `should save article successfully`() {

        val newArticle = createArtickleAndMocks()

        val monoResult = articleService.createArticle(newArticle)

        StepVerifier.create(monoResult)
            .assertNext { article ->
                assertEquals("Saved Test Title", article.title)
                assertEquals("Test Content", article.content)
            }
            .verifyComplete()
    }

    @Test
    fun `should return redditPost links`() {
        val newArticle = createArtickleAndMocks()

        val fluxResult = articleService.getRedditPostLinksByTitle(newArticle.title)

        StepVerifier.create(fluxResult)
            .assertNext { link ->
                assertEquals("link/to/title/1", link)
            }
            .assertNext { link ->
                assertEquals("link/to/title/2", link)
            }
            .verifyComplete()
    }
}