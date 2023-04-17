package com.aryan.UrlShortnerInKt

import com.aryan.UrlShortnerInKt.bean.Url
import com.aryan.UrlShortnerInKt.bean.UrlReport
import com.aryan.UrlShortnerInKt.exception.UrlLengthException
import com.aryan.UrlShortnerInKt.exception.UrlNotFoundException
import com.aryan.UrlShortnerInKt.exception.UrlTimeoutException
import com.aryan.UrlShortnerInKt.repository.UrlReportRepository
import com.aryan.UrlShortnerInKt.repository.UrlRepository
import com.aryan.UrlShortnerInKt.service.UrlService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime


@ExtendWith(MockitoExtension::class)
class UrlServiceRedirectTest {
    @Mock
    lateinit var urlRepo: UrlRepository

    @Mock
    lateinit var reportRepo: UrlReportRepository

    @InjectMocks
    lateinit var  service: UrlService

    @Test
    fun shortUrlNotHavingMiniurl() {
        val mockShortUrl = "a7f7386"
        //String mockLongUrl  = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation";
        //Url mockUrl =new Url("1","Aryan",mockLongUrl,mockShortUrl,LocalDateTime.now(),LocalDateTime.now()) ;
        Mockito.`when`(urlRepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.empty())

        val response: Mono<String> = service.redirect(mockShortUrl).doOnNext(System.out::println)

        StepVerifier
            .create<Any>(response)
            .verifyError(UrlLengthException::class.java)
    }

    @Test
    fun shortUrlNotInDb() {
        val mockShortUrl = "a7f73864"
        //String mockLongUrl  = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation";
        //Url mockUrl =new Url("1",mockLongUrl,mockShortUrl,LocalDateTime.now(),LocalDateTime.now()) ;
        Mockito.`when`(urlRepo.findByShortUrl(mockShortUrl)).thenReturn(
            Mono.empty()
        )
        val response: Mono<String> = service.redirect(mockShortUrl).doOnNext(System.out::println)
        StepVerifier
            .create(response)
            .verifyError(UrlNotFoundException::class.java)
    }

    @Test
    fun urlExpired() {
        val mockShortUrl = "a7f73864"
        val mockLongUrl = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation"
        val mockUrl = Url("1", "Aryan", mockLongUrl, mockShortUrl, LocalDateTime.now(), LocalDateTime.now().minusSeconds(5))
        Mockito.`when`(urlRepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(urlRepo.delete(mockUrl)).thenReturn(Mono.empty())
        val response: Mono<String> = service.redirect(mockShortUrl).doOnNext(System.out::println)
        StepVerifier
            .create(response)
            .verifyError(UrlTimeoutException::class.java)
    }

    @Test
    fun urlNotExpired() {
        val mockShortUrl = "a7f73864"
        val mockLongUrl = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation"
        val mockUrl = Url("1", "Aryan", mockLongUrl, mockShortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        val urlReport = UrlReport("1", mockShortUrl, LocalDate.now(), LocalDate.now(), 0)
        Mockito.`when`(urlRepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportRepo.findByShortUrlAndFetchDate(mockShortUrl, LocalDate.now()))
            .thenReturn(Mono.just(urlReport))
        Mockito.`when`(reportRepo.save(Mockito.any(UrlReport::class.java)))
            .thenReturn(Mono.just(UrlReport("1", mockShortUrl, LocalDate.now(), LocalDate.now(), 1)))
        //Mockito.when(urlRepo.delete(mockUrl)).thenReturn(Mono.empty());
        val response: Mono<String> = service.redirect(mockShortUrl).doOnNext(System.out::println)
        StepVerifier
            .create(response)
            .expectNext(mockLongUrl)
            .verifyComplete()
    }
}