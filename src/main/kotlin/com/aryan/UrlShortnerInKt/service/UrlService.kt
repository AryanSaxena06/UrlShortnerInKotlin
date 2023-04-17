package com.aryan.UrlShortnerInKt.service

import com.aryan.UrlShortnerInKt.bean.Url
import com.aryan.UrlShortnerInKt.bean.UrlDto
import com.aryan.UrlShortnerInKt.bean.UrlReport
import com.aryan.UrlShortnerInKt.exception.*
import com.aryan.UrlShortnerInKt.repository.UrlReportRepository
import com.aryan.UrlShortnerInKt.repository.UrlRepository
import com.google.common.hash.Hashing
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UrlService {
    @Autowired
    lateinit var repo:UrlRepository
    @Autowired
    lateinit var reportRepo:UrlReportRepository
    var domain="http://localhost:8080/miniurl.com/"

    fun generateShortUrl(urlDto: UrlDto): Mono<String> {
         return Mono.just(urlDto)
             .flatMap { urlD-> checkLongUrl(urlD) }
             .flatMap { urldto-> findUrlInDbOrCreateUrl(urldto)}
             .flatMap { url-> findReportInDbOrCreateReport(url)
             .then(Mono.just("UserId = "+url.userId+"\n"+ "ShortUrl = " +domain+url.shortUrl))}
    }


    //check if long url is blank , contains MiniUrl or its length <50
    private fun checkLongUrl(urlDto: UrlDto): Mono<UrlDto> {
        return Mono.just(urlDto)
            .filter { urlDtoData-> !urlDtoData.userId.isNullOrBlank() }
            .switchIfEmpty(Mono.error(UserNotValidException("Blank or Null userId is not accepted")))
            .filter{urlD-> UrlValidator.getInstance().isValid(urlD.longUrl) && !urlD.longUrl!!.contains("miniurl")}
            .switchIfEmpty(Mono.error(ArgumentNotValidException("Please Provide Valid Long Url")))
            .filter{urlD->urlD.longUrl!!.length>50}
                .switchIfEmpty(Mono.error(UrlLengthException("Enter a Long Url of length more than 50 characters")))
    }

    private fun findUrlInDbOrCreateUrl(urldto: UrlDto):Mono<Url> {
        return repo.findByLongUrlAndUserId(urldto.longUrl,urldto.userId)
            .switchIfEmpty(createUrl(urldto))
    }

    private fun findReportInDbOrCreateReport(url: Url):Mono<UrlReport> {
        return  reportRepo.findByShortUrl(url.shortUrl)
            .switchIfEmpty(createUrlReport(url))
    }

    private fun createUrl(urlDto: UrlDto): Mono<Url> {
        val url = Url(
            null,
            urlDto.userId,
            urlDto.longUrl,
            encodeUrl(urlDto.longUrl),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        )
        return repo.save(url)
    }

    private fun encodeUrl(longUrl: String?): String {
        var encodeUrl:String=""
        val time:LocalDateTime = LocalDateTime.now()
         encodeUrl=Hashing.murmur3_32_fixed().hashString(longUrl.plus(time.toString()),StandardCharsets.UTF_8).toString()
        return encodeUrl;
    }

    private fun createUrlReport(url: Url): Mono<UrlReport> {
        val urlReport = UrlReport(
            null,
            url.shortUrl,
            LocalDate.now(),
            url.creationDate.toLocalDate(),
            0
        )
        return reportRepo.save(urlReport)
    }

    fun redirect(shortUrl: String):Mono<String> {
        return Mono.just(shortUrl)

            .flatMap { shorturl-> checkShortUrlLengthOrInDbOrExpired(shorturl) }
            .flatMap { url-> reportRepo.findByShortUrlAndFetchDate(shortUrl,LocalDate.now())
                        .flatMap { urlReport->updateHits(urlReport) }
                            .switchIfEmpty(createNewUrlReport(url))
                                .then(Mono.just(url.longUrl!!))
                      }

    }

    private fun checkShortUrlLengthOrInDbOrExpired(shorturl: String):Mono<Url> {
        return Mono.just(shorturl).filter{shorturl->shorturl.length==8}
            .switchIfEmpty(Mono.error(UrlLengthException(" Short Url is less than 8 characters")))

            .flatMap{ shorturl->getEncodedUrlFromDb(shorturl) }
            .switchIfEmpty(Mono.error(UrlNotFoundException("short Url not present in db")))

            .filter{url->url.expirationDate.isAfter(LocalDateTime.now())}
            .switchIfEmpty(deleteShortUrl(shorturl)
                .then(Mono.error(UrlTimeoutException("Url expired"))))
    }

    private fun getEncodedUrlFromDb(shorturl: String):Mono<Url> {
        return repo.findByShortUrl(shorturl)
    }

    private fun deleteShortUrl(shorturl:String): Mono<Void> {
        return repo.findByShortUrl(shorturl)
                .flatMap { url->repo.delete(url) }
    }

     fun updateHits(urlReport: UrlReport):Mono<UrlReport> {
         urlReport.hits=urlReport.hits+1
         return reportRepo.save(urlReport)
    }

    private fun createNewUrlReport(url:Url ): Mono<UrlReport> {
        val urlReport =UrlReport(null,url.shortUrl,LocalDate.now(),
            url.creationDate.toLocalDate(),1)
        return reportRepo.save(urlReport)
    }

    fun getAllByCreateDate(createDate: LocalDate?):Flux<UrlReport> {
        return reportRepo.findByCreateDateAndFetchDate(createDate,createDate)
    }

    fun getAllUrlByHits(fetchDate: LocalDate): Flux<UrlReport> {
        return reportRepo.findByFetchDate(fetchDate).filter( { urlRport-> urlRport.hits>0 })
    }

    fun getAllReports(): Flux<UrlReport> {
        return reportRepo.findAll()
    }

}