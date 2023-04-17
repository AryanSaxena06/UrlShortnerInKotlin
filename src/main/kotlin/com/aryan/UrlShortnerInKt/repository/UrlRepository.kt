package com.aryan.UrlShortnerInKt.repository

import com.aryan.UrlShortnerInKt.bean.Url
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UrlRepository:ReactiveMongoRepository<Url,String> {
     fun findByLongUrlAndUserId(longUrl: String?, userId: String?):Mono<Url>
     fun findByShortUrl(shorturl: String):Mono<Url>
}