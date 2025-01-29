
package com.stacs.article.analysis.backend.User

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectUserServiceTest {


    private lateinit var projectUserRepository: ProjectUserRepository
    private lateinit var projectUserService: ProjectUserService

    @BeforeEach
    fun setup() {
        projectUserRepository = mockk<ProjectUserRepository>()
        projectUserService = ProjectUserService(projectUserRepository)
    }

    private fun createProjectUserAndMocks() {

        val user = ProjectUser(
            name = "Julia",
            savedSources = mutableListOf("source1", "source2"),
            savedArticles =  mutableListOf()
        )


        every { projectUserRepository.findById(any<String>()) } returns Mono.just(user)
    }


    @Test
    fun `should return sources`() {

        createProjectUserAndMocks()

        val monoResult = projectUserService.getSourcesFromUserByName("newRedditPost")

        StepVerifier.create(monoResult)
            .assertNext { sources ->
                assertEquals(listOf("source1", "source2"), sources)
            }
            .verifyComplete()
    }
}
