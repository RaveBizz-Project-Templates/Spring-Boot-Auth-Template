package com.rave.auth.models.params

data class RegisterParams(
  val username: String,
  val email: String,
  val password: String,
)