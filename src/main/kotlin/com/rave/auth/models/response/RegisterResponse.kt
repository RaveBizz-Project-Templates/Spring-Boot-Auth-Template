package com.rave.auth.models.response

import com.rave.auth.entities.AppUser

data class RegisterResponse(
  val user: AppUser,
  val accessToken: String,
)
