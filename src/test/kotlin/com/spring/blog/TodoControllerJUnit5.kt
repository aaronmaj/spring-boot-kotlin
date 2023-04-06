package com.spring.blog

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = arrayOf(WiremockInitializer::class))
class TodoControllerJUnit5(@Autowired val webTestClient: WebTestClient, @Autowired val wireMockServer: WireMockServer) {

    @AfterEach
    fun reset() {
        wireMockServer.resetAll()
    }

    @Test
    fun wireMockRequestMatchingWithData() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/todos")).atPriority(1).willReturn(
                WireMock.aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("todo-api/response-200.json")
            )
        )

        this.webTestClient.get().uri("/api/todos").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().is2xxSuccessful.expectBody()
            .jsonPath("$.length()").isEqualTo(3).jsonPath("$[0].title").isEqualTo("delectus aut autem")

        wireMockServer.verify(
            WireMock.exactly(1),
            WireMock.getRequestedFor(WireMock.urlEqualTo("/todos")).withHeader("Accept", WireMock.equalTo("application/json"))
                .withHeader(HttpHeaders.USER_AGENT, WireMock.equalTo("SpringBootKotlinApplication"))
        )

        val events: List<ServeEvent> = wireMockServer.allServeEvents
        events[0].request.containsHeader(HttpHeaders.USER_AGENT)

        val unMatchedRequests = wireMockServer.findAllUnmatchedRequests()

        Assertions.assertEquals(0, unMatchedRequests.size)
    }
}