package com.spring.blog.repository

import com.spring.blog.model.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class PersonInitilizer(@Autowired val personRepository: PersonRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        personRepository.deleteAll()
        val personOne = Person(
            null, "Mike", "Kotlin", "mk90",
            LocalDate.of(1990, 1, 1)
        )
        val personTwo = Person(
            null, "Java", "Duke", "jduke",
            LocalDate.of(1995, 1, 1)
        )
        val personThree = Person(
            null, "Andy", "Fresh", "afresh",
            LocalDate.of(2000, 1, 1)
        )

        personRepository.saveAll(listOf(personOne, personTwo, personThree))
    }
}
