package com.aryan.UrlShortnerInKt

import com.aryan.UrlShortnerInKt.bean.Url
import com.aryan.UrlShortnerInKt.bean.UrlDto
import com.aryan.UrlShortnerInKt.bean.UrlReport
import com.aryan.UrlShortnerInKt.exception.ArgumentNotValidException
import com.aryan.UrlShortnerInKt.exception.UrlLengthException
import com.aryan.UrlShortnerInKt.repository.UrlReportRepository
import com.aryan.UrlShortnerInKt.repository.UrlRepository
import com.aryan.UrlShortnerInKt.service.UrlService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UrlServiceCreateTest :UrlShortnerInKtApplicationTests(){
    @Mock
    lateinit var urlRepo: UrlRepository

    @Mock
    lateinit var reportRepo: UrlReportRepository

    @InjectMocks
    lateinit var  service: UrlService

    @Test
    fun blankLongUrlTest()
    {
        val urlDto =UrlDto("","Aryan")
        val response = service.generateShortUrl(urlDto)

        StepVerifier
            .create(response)
            .verifyError(ArgumentNotValidException::class.java)
    }

    @Test
    fun longUrlContainsMiniurl() {
        val urlDto = UrlDto("miniurl", "Aryan")
        val response: Mono<String> = service.generateShortUrl(urlDto).doOnNext { x: String? -> println(x)
        }
        StepVerifier
            .create<Any>(response)
            .verifyError(ArgumentNotValidException::class.java)
    }

    @Test
    fun longUrlAlreadyShort() {
        val urlDto = UrlDto("LongUrltooShort.com", "Aryan")
        val response = service.generateShortUrl(urlDto).doOnNext { x: String? -> println(x) }

        StepVerifier
            .create(response)
            .verifyError(UrlLengthException::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun WhenNotInUrlDbAndNotInReportDb()
    {
        val domain = "http://localhost:8080/miniurl.com/"
        val mockLongUrl = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation"
        val mockUrlDto = UrlDto(mockLongUrl, "Aryan")
        val mockShortUrl = "a7f73864"
        val mockUrl = Url("1", "Aryan", mockLongUrl, mockShortUrl, LocalDateTime.now(), LocalDateTime.now())
        val mockUrlResponse = """
                ${"UserId = " + mockUrlDto.userId}
                ShortUrl = $domain$mockShortUrl
                """.trimIndent()
        val mockUrlReport = UrlReport("1", mockShortUrl, LocalDate.now(), LocalDate.now(), 0)
        Mockito.`when`(urlRepo.findByLongUrlAndUserId(mockLongUrl, "Aryan")).thenReturn(Mono.empty())
        Mockito.`when`(urlRepo.save(Mockito.any(Url::class.java))).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportRepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.empty())
        Mockito.`when`(reportRepo.save(Mockito.any(UrlReport::class.java))).thenReturn(Mono.just(mockUrlReport))
        val response = service.generateShortUrl(mockUrlDto).doOnNext { x: String? -> println(x) }
        StepVerifier
            .create(response)
            .expectNext(mockUrlResponse)
            .verifyComplete()
    }

    @Test
    fun WhenInUrlDbAndInReportDb() {
        val domain = "http://localhost:8080/miniurl.com/"
        val mockLongUrl = "https://www.digitalocean.com/community/tutorials/spring-configuration-annotation"
        val mockUrlDto:UrlDto = UrlDto(mockLongUrl, "Aryan")
        val mockShortUrl = "a7f73864"
        val mockUrl:Url = Url("1", "Aryan", mockLongUrl, mockShortUrl, LocalDateTime.now(), LocalDateTime.now())
        val mockUrlResponse = """
                ${"UserId = " + mockUrlDto.userId}
                ShortUrl = $domain$mockShortUrl
                """.trimIndent()
        val mockUrlReport:UrlReport = UrlReport("1", mockShortUrl, LocalDate.now(), LocalDate.now(), 0)
        Mockito.`when`(urlRepo.findByLongUrlAndUserId(mockLongUrl, "Aryan")).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(urlRepo.save(Mockito.any(Url::class.java))).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportRepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(mockUrlReport))
        Mockito.`when`(reportRepo.save(Mockito.any(UrlReport::class.java))).thenReturn(Mono.just(mockUrlReport))
        val response = service.generateShortUrl(mockUrlDto).doOnNext { x: String? -> println(x) }
        StepVerifier
            .create(response)
            .expectNext(mockUrlResponse)
            .verifyComplete()
    }

    @Test
    fun getAllByCreateDate() {
        val date = LocalDate.now()
        val ShortUrl = "a7f73864"
        val urlReport = UrlReport("1", ShortUrl, date, date, 1)
        Mockito.`when`(reportRepo.findByCreateDateAndFetchDate(date, date)).thenReturn(Flux.just(urlReport))
        val response: Flux<UrlReport> = service.getAllByCreateDate(date).doOnNext(System.out::println)
        StepVerifier
            .create(response)
            .expectNext(urlReport)
            .expectComplete()
            .verify()
    }

    @Test
    fun getAllUrlByHits()
    {
        val date:LocalDate =LocalDate.now()
        val shortUrl ="a7f73864"
        val urlReport:UrlReport = UrlReport("1",shortUrl,date,date,1)

        Mockito.`when`(reportRepo.findByFetchDate(date)).thenReturn(Flux.just(urlReport))

        val response =service.getAllUrlByHits(date)

        StepVerifier
            .create(response)
            .expectNext(urlReport)
            .expectComplete()
            .verify();
    }

}