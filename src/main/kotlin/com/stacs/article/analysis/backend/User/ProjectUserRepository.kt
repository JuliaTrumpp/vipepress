package com.stacs.article.analysis.backend.User

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectUserRepository : ReactiveCrudRepository<ProjectUser, String> {}
