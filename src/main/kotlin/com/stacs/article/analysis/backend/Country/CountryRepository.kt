package com.stacs.article.analysis.backend.Country

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CountryRepository : ReactiveCrudRepository<Country, String>{
    @Query("SELECT * FROM COUNTRY WHERE COUNTRY.short = ?")
    fun findByShort(short: String): Mono<Country>
}