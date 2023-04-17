package com.aryan.UrlShortnerInKt.controller

import com.aryan.UrlShortnerInKt.bean.UrlDto
import com.aryan.UrlShortnerInKt.bean.UrlReport
import com.aryan.UrlShortnerInKt.service.UrlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDate

@RestController
class UrlShortenerController {

    @Autowired
    private lateinit var service: UrlService;

    @PostMapping("/create")
    fun generateShortUrl(@RequestBody urlDto:UrlDto):Mono<String>
    {
        return service.generateShortUrl(urlDto)
    }

    @GetMapping("miniurl.com/{shortUrl}")
    fun redirectToLongUrl(@PathVariable shortUrl:String,response:ServerHttpResponse):Mono<Void>
    {
        return service.redirect(shortUrl)
            .flatMap { longUrl->
                response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                response.getHeaders()?.setLocation(URI.create(longUrl))
                return@flatMap response.setComplete()
            }

    }

    @GetMapping("/getUrlByCreationDate")
    fun getAllByCreateDate(@RequestParam(value = "createDate", required = false) createDate: LocalDate?): Flux<UrlReport>
    {
        //createDate?:println("null")
        if(createDate==null)
        {
            return service.getAllReports()
        }
        return service.getAllByCreateDate(createDate)
    }

    @GetMapping("/UrlByHits")
    fun getAllByHits(@RequestParam(value = "fetchDate", required = false) fetchDate: LocalDate ?):Flux<UrlReport>
    {
        if(fetchDate==null)
        {
            return service.getAllReports()
        }
        return service.getAllUrlByHits(fetchDate)
    }

    @GetMapping("/findAllUrlReport")
    fun getAllUrlReport():Flux<UrlReport>
    {
        return service.getAllReports()
    }

}