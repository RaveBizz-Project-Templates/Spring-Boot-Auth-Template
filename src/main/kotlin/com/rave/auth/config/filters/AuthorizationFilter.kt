package com.rave.auth.config.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.rave.auth.models.ResponseStatus
import com.rave.auth.utils.Constants.SECRET
import io.jsonwebtoken.Jwts
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val authorizationHeader = request.getHeader(AUTHORIZATION)
    if (authorizationHeader.startsWith("Bearer ")) {
      try {
        val token = authorizationHeader.substring("Bearer ".length)
        val decodedJwt = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).body
        val username = decodedJwt.subject
        val role = decodedJwt["role"]
        val authorities = mutableListOf(SimpleGrantedAuthority(role.toString()))
        val authToken = UsernamePasswordAuthenticationToken(
          username,
          null,
          authorities
        )
        authToken.authorities
        SecurityContextHolder.getContext().authentication = authToken
        filterChain.doFilter(
          request,
          response
        )
      } catch (e: Exception) {
        val errorMessage = e.message ?: "Something went wrong"
        response.status = 401
        response.addHeader("Content-Type", "application/json")
        ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValue(
          response.outputStream,
          ResponseStatus.Error(message = errorMessage)
        )
      }
    } else {
      filterChain.doFilter(
        request,
        response
      )
    }
  }
}