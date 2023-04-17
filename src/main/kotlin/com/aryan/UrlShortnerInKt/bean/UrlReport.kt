package com.aryan.UrlShortnerInKt.bean

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "urlReport")
data class UrlReport(@Id val id: String?, val shortUrl:String, @JsonFormat(pattern = "yyyy-MM-dd") val fetchDate:LocalDate
                     , @JsonFormat(pattern = "yyyy-MM-dd") val createDate:LocalDate, var hits:Long) {
}