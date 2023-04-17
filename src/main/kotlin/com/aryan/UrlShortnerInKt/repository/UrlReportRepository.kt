package com.aryan.UrlShortnerInKt.repository

import com.aryan.UrlShortnerInKt.bean.Url
import com.aryan.UrlShortnerInKt.bean.UrlReport
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository
interface UrlReportRepository:ReactiveMongoRepository<UrlReport,String> {
    fun findByShortUrl(shortUrl: String): Mono<UrlReport>
    fun findByShortUrlAndFetchDate(shortUrl: String, date: LocalDate):Mono<UrlReport>
    fun findByCreateDateAndFetchDate(date: LocalDate?, date1: LocalDate?): Flux<UrlReport>
    fun findByFetchDate(fetchDate: LocalDate): Flux<UrlReport>
}