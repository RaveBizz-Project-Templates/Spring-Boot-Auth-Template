package com.rave.auth.config

import com.rave.auth.config.filters.AuthorizationFilter
import com.rave.auth.services.AppUserService
import com.rave.auth.utils.RoutePermissionsLists.authenticatedRoutes
import com.rave.auth.utils.RoutePermissionsLists.unauthenticatedRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfiguration constructor(
  private val appUserService: AppUserService,
  private var bCryptPasswordEncoder: BCryptPasswordEncoder,
) {
  @Bean
  @Order(0)
  @Throws(Exception::class)
  fun resources(http: HttpSecurity): SecurityFilterChain {
    http.requestMatchers { matchers ->
      matchers.antMatchers(
        *unauthenticatedRoutes.map { it }.toTypedArray()
      )
    }.authorizeHttpRequests { authZ ->
      authZ.anyRequest().permitAll()
    }.requestCache().disable().securityContext().disable().sessionManagement().disable().csrf().disable().cors()

    return http.build()
  }

  @Bean
  @Throws(Exception::class)
  fun filterChain(
    http: HttpSecurity,
    authenticationManager: AuthenticationManager,
  ): SecurityFilterChain {
    http.requestMatchers { matchers ->
      matchers.antMatchers(
        *authenticatedRoutes.map { it }.toTypedArray(),
      )
    }.authorizeHttpRequests { authZ ->
      authZ.anyRequest().authenticated()
    }.addFilterBefore(
      AuthorizationFilter(),
      UsernamePasswordAuthenticationFilter::class.java
    ).csrf().disable()
    return http.build()
  }

  @Bean
  @Throws(java.lang.Exception::class)
  fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
    return authenticationConfiguration.authenticationManager
  }

  @Throws(java.lang.Exception::class)
  protected fun configure(auth: AuthenticationManagerBuilder) {
    auth.authenticationProvider(daoAuthenticationProvider())
  }

  @Bean
  fun daoAuthenticationProvider(): DaoAuthenticationProvider {
    val provider = DaoAuthenticationProvider()
    provider.setPasswordEncoder(bCryptPasswordEncoder)
    provider.setUserDetailsService(appUserService)
    return provider
  }
}