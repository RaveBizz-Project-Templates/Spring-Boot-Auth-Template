package com.rave.auth.services

import com.rave.auth.entities.AppUser
import com.rave.auth.repository.AppUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AppUserService(private val appUserRepository: AppUserRepository) : UserDetailsService {
  fun register(appUser: AppUser): AppUser {
    return appUserRepository.save(appUser)
  }

  fun findByEmail(email: String): AppUser? {
    return appUserRepository.findByEmail(email)
  }

  fun findByUsername(username: String): AppUser? {
    return appUserRepository.findByUsername(username)
  }

  fun deleteAccount(appUser: AppUser) {
    appUserRepository.delete(appUser)
  }

  override fun loadUserByUsername(username: String?): UserDetails {
    TODO("Not yet implemented")
  }
}