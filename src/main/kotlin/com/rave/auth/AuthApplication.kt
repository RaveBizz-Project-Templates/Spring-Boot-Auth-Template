package com.rave.auth

import com.rave.auth.models.ResponseStatus
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class AuthApplication

@RestController
class HelloController {
  @GetMapping("hello")
  fun hello() = ResponseStatus.Success(data = "hello")
}

fun main(args: Array<String>) {
  runApplication<AuthApplication>(*args)
}
