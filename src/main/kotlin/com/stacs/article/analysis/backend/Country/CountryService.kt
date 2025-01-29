package com.stacs.article.analysis.backend.Country

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CountryService(private val repository: CountryRepository) {
    fun getCountryByShort(short: String): Mono<Country> {
        return repository.findByShort(short)
            .switchIfEmpty(Mono.error(RuntimeException("Country not found for short: $short")))
    }
}