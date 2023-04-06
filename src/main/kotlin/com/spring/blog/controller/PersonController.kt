package com.spring.blog.controller

import com.fasterxml.jackson.databind.node.ArrayNode
import com.spring.blog.model.Person
import com.spring.blog.repository.PersonRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/api")
class PersonController(
    val webClient: WebClient,
    val personRepository: PersonRepository
) {

    @GetMapping(value = ["/todos"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodos() = webClient
        .get()
        .uri("/todos")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ArrayNode::class.java)
        .block()

    @GetMapping(value = ["/persons"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPersons(): List<Person> = personRepository.findAll()
}