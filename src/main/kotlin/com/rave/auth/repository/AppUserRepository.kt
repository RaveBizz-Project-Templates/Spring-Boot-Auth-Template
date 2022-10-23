package com.rave.auth.repository

import com.rave.auth.entities.AppUser
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserRepository : JpaRepository<AppUser, Int> {
  fun findByEmail(email: String): AppUser?
  fun findByUsername(username: String): AppUser?
}