package com.aryan.UrlShortnerInKt.exception

data class ErrorDetails(var errorDetails:String, var message:String?) {
    constructor():this("","")
}