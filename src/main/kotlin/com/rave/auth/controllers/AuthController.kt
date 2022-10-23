package com.rave.auth.controllers

import com.rave.auth.models.ResponseStatus
import com.rave.auth.entities.AppUser
import com.rave.auth.models.params.LoginParams
import com.rave.auth.models.params.RegisterParams
import com.rave.auth.models.response.DeleteMyAccountResponse
import com.rave.auth.models.response.RegisterResponse
import com.rave.auth.models.response.TokenResponse
import com.rave.auth.services.AppUserService
import com.rave.auth.utils.decodeJwt
import com.rave.auth.utils.generateJwt
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("api/v1")
class AuthController(
  private val appUserService: AppUserService,
  private val passwordEncoder: PasswordEncoder,
) {
  @PostMapping(value = ["register"])
  fun register(@RequestBody body: RegisterParams): ResponseEntity<ResponseStatus<RegisterResponse>> {
    var user = AppUser()
    user.username = body.username
    user.avatar = "https://robohash.org/" + body.username + ".png"
    user.email = body.email
    user.password = passwordEncoder.encode(body.password)
    user = appUserService.register(user)
    val uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/register").toUriString())
    return ResponseEntity.created(uri).body(
      ResponseStatus.Success(
        data = RegisterResponse(
          user,
          accessToken = generateJwt(
            issuer = user.id.toString(),
            subject = user.username
          )
        )
      )
    )
  }

  @PostMapping(value = ["login"])
  fun login(@RequestBody body: LoginParams): ResponseEntity<ResponseStatus<TokenResponse>> {
    val user = appUserService.findByEmail(body.email) ?: return ResponseEntity.badRequest()
      .body(ResponseStatus.Error(message = "A user with that email does not exist. Try registering."))

    if (!passwordEncoder.matches(
        body.password,
        user.password
      )
    ) {
      return ResponseEntity.badRequest().body(ResponseStatus.Error(message = "The password is incorrect, try again."))
    }

    return ResponseEntity.ok(
      ResponseStatus.Success(
        data = TokenResponse(
          accessToken = generateJwt(
            issuer = user.id.toString(),
            subject = user.username
          )
        )
      )
    )
  }

  @GetMapping(value = ["me"])
  fun me(@RequestHeader headers: HttpHeaders): ResponseEntity<ResponseStatus<AppUser>> {
    val decodedJwt = headers[AUTHORIZATION]?.get(0)
      ?.let { authorizationHeader ->
        decodeJwt(authorizationHeader)
      }!!
    val user = appUserService.findByUsername(decodedJwt.username)
      ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ResponseStatus.Error(message = "This user no longer exists, or never existed."))

    return ResponseEntity.ok(ResponseStatus.Success(data = user))
  }

  @DeleteMapping(value = ["me"])
  fun deleteMyAccount(@RequestHeader headers: HttpHeaders): ResponseEntity<ResponseStatus<Any>> {
    val decodedJwt = headers[AUTHORIZATION]?.get(0)
      ?.let { authorizationHeader ->
        decodeJwt(authorizationHeader)
      }!!
    val user = appUserService.findByUsername(decodedJwt.username)
      ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ResponseStatus.Error(message = "This user no longer exists, or never existed."))

    return try {
      appUserService.deleteAccount(user)
      ResponseEntity.ok(ResponseStatus.Success(data = DeleteMyAccountResponse(wasDeleted = true)))
    } catch (e: Exception) {
      ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ResponseStatus.Error(message = e.message.toString()))
    }
  }
}