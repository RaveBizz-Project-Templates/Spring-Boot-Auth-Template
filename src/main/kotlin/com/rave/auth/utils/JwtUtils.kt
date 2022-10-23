package com.rave.auth.utils

import com.rave.auth.models.DecodedJwt
import com.rave.auth.utils.Constants.SECRET
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

fun generateJwt(
  issuer: String,
  subject: String,
  days: Int = 1,
): String {
  val claims = HashMap<String, Any>()
  claims["role"] = "ROLE_USER"
  claims["user_id"] = issuer
  claims["username"] = subject
  return Jwts.builder()
    .setIssuer(issuer)
    .setSubject(subject)
    .setClaims(
      claims
    )
    .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * days * 1000))
    .signWith(
      SignatureAlgorithm.HS512,
      SECRET
    ).compact()
}

fun decodeJwt(jwt: String): DecodedJwt {
  val token = jwt.substring("Bearer ".length)
  val decodedJwt = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).body
  return DecodedJwt(
    username = decodedJwt["username"].toString()
  )
}