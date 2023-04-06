package com.spring.blog.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "persons")
class Person(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false) var firstname: String,
    @Column(nullable = false) var lastname: String,
    @Column(unique = true, nullable = false) var username: String,
    @Column(name = "date_of_birth", nullable = false) var dateOfBirth: LocalDate
)