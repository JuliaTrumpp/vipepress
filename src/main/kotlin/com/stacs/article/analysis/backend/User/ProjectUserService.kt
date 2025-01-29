package com.stacs.article.analysis.backend.User

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProjectUserService(private val repository: ProjectUserRepository) {
    fun getSourcesFromUserByName(name: String): Mono<List<String>> {
        return repository.findById(name)
            .map { user -> user.savedSources }
    }
}