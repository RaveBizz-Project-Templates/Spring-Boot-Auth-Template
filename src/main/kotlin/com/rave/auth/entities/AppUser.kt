package com.rave.auth.entities

import javax.persistence.*

@Entity
class AppUser {
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = 0

  @Column(nullable = false)
  var username = ""

  @Column(nullable = false)
  var avatar = ""

  @Column(
    unique = true,
    nullable = false
  )
  var email = ""

  @Column(nullable = false)
  var password = ""
}