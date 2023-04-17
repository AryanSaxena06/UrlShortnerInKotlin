package com.aryan.UrlShortnerInKt.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
@Document(collection = "url")
data class Url(@Id val id: String?,
               val userId:String?,
               val longUrl:String?,
               val shortUrl:String,
               val creationDate:LocalDateTime,
               val expirationDate:LocalDateTime) {
}