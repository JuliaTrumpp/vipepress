package com.stacs.article.analysis.backend.Reddit.Comment

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RedditCommentRepository: ReactiveCrudRepository<RedditComment, Long> {


}