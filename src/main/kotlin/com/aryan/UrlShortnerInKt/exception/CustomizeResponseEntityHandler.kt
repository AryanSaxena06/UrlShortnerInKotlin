package com.aryan.UrlShortnerInKt.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomizeResponseEntityHandler {

    @ExceptionHandler(ArgumentNotValidException::class)
    fun notValidArgument(ex:Exception):ResponseEntity<ErrorDetails>
    {
        var error =  ErrorDetails()
        error.errorDetails="Given Url is Not valid"
        error.message=ex.message
        return ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun urlNotFound(ex:Exception):ResponseEntity<ErrorDetails>
    {
        val error =  ErrorDetails("your short url is incorrect",ex.message)
        return ResponseEntity<ErrorDetails>(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UrlTimeoutException::class)
    fun urlTimeOut(ex:Exception):ResponseEntity<ErrorDetails>
    {
        val error =  ErrorDetails("url no longer exist",ex.message)
        return ResponseEntity<ErrorDetails>(error, HttpStatus.GATEWAY_TIMEOUT)
    }

    @ExceptionHandler(UrlLengthException::class)
    fun urlLenghtShort(ex:Exception):ResponseEntity<ErrorDetails>
    {
        val error =  ErrorDetails("url too short ",ex.message)
        return ResponseEntity<ErrorDetails>(error, HttpStatus.NOT_ACCEPTABLE)
    }
    @ExceptionHandler(UserNotValidException::class)
    fun userBlankOrNull(ex:Exception):ResponseEntity<ErrorDetails>
    {
        val error =  ErrorDetails("Not valid ",ex.message)
        return ResponseEntity<ErrorDetails>(error, HttpStatus.NOT_ACCEPTABLE)
    }
}