package com.rave.auth.utils

object RoutePermissionsLists {
  val unauthenticatedRoutes = arrayOf(
    "/api/v*/login/**",
    "/api/v*/register/**",
  )
  val authenticatedRoutes = arrayOf(
    "/hello/**",
    "/api/v*/me/**",
  )
}