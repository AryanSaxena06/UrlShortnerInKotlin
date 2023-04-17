package com.aryan.UrlShortnerInKt

import com.aryan.UrlShortnerInKt.bean.UrlDto
import com.aryan.UrlShortnerInKt.bean.UrlReport
import com.aryan.UrlShortnerInKt.controller.UrlShortenerController
import com.aryan.UrlShortnerInKt.service.UrlService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
//import org.springframework.http.HttpStatus
//import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.URI
import java.time.LocalDate
//import org.springframework.http.server.reactive.DefaultServerHttpResponse


@ExtendWith(MockitoExtension::class)
class ControllerTest {
    @Mock
    lateinit var service :UrlService

    @InjectMocks
    lateinit var controller : UrlShortenerController

    //lateinit var response: ServerHttpResponse

    @Test
    fun generateTest() {
        val urlDto = UrlDto("Aryan", "")
        val mockShortUrl = "http://localhost:8080/miniurl.com/a7f73864"

        Mockito.`when`(service.generateShortUrl(urlDto)).thenReturn(Mono.just(mockShortUrl))
        val result: Mono<String> = controller.generateShortUrl(urlDto)

        StepVerifier
            .create<Any>(result)
            .expectNext(mockShortUrl)
            .verifyComplete()
    }

//    @Test
//    fun getTest() {
//        //UrlDto urlDto =new UrlDto("Aryan","");
//        val ShortUrl = "a7f73864"
//        //String mockFullShortUrl ="http://localhost:8080/miniurl.com/a7f73864";
//        val mockLongUrl = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation"
//        var response:ServerHttpResponse
//        //response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
////        response.getHeaders().setLocation(URI.create(mockLongUrl))
////        response.setComplete()
//
//        Mockito.`when`(service.redirect(ShortUrl)).thenReturn(Mono.just(mockLongUrl))
//        val result = controller.redirectToLongUrl(ShortUrl)
//        StepVerifier
//            .create(result)
//            .expectComplete()
//    }

    @Test
    fun getUrlByCreationDate() {
        val ShortUrl = "a7f73864"
        val date: LocalDate = LocalDate.now()
        val urlReport = UrlReport("1", ShortUrl, date, date, 0)
        Mockito.`when`(service.getAllByCreateDate(date)).thenReturn(Flux.just(urlReport))
        val result: Flux<UrlReport> = controller.getAllByCreateDate(date)
        StepVerifier
            .create(result)
            .expectNext(urlReport)
            .expectComplete()
            .verify()
    }

    @Test
    fun UrlByHits() {
        val ShortUrl = "a7f73864"
        val date = LocalDate.now()
        val urlReport = UrlReport("1", ShortUrl, date, date, 1)
        Mockito.`when`(service.getAllUrlByHits(date)).thenReturn(Flux.just(urlReport))
        val result = controller.getAllByHits(date)
        StepVerifier
            .create(result)
            .expectNext(urlReport)
            .expectComplete()
            .verify()
    }
}