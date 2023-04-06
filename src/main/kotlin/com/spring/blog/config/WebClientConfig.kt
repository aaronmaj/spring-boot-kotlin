package com.spring.blog.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {


    @Bean
    fun webClient(
        @Value("\${todo_base_url}") todoBaseUrl: String, builder: WebClient.Builder
    ) = builder
        .clone()
        .baseUrl(todoBaseUrl)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "SpringBootKotlinApplication")
        .build()
}