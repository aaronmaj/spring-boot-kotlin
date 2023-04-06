package com.spring.blog

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ManualSetupIT(@Autowired val webTestClient: WebTestClient) {

    companion object {
        @JvmStatic
        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

        @JvmStatic
        @BeforeAll

        fun startWiremockServer() {
            wireMockServer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("todo_base_url") { mockUrl }

        }

        private val mockUrl: String
            get() = wireMockServer.url("").toString()


        @JvmStatic
        @AfterAll
        fun stopWireMockServer() {
            wireMockServer.start()
        }

    }

    @BeforeEach
    fun clearWiremock() {
        println("Stored stubbing before reset:  " + wireMockServer.getStubMappings().size)
        wireMockServer.resetAll()
        println("Stored stubbing after reset:  " + wireMockServer.getStubMappings().size)

    }

    @Test
    fun testWireMock() {
        println(wireMockServer.baseUrl())
        Assertions.assertTrue(wireMockServer.isRunning)
    }

    @Test
    @Order(1)
    fun basicWireMockExample() {
        wireMockServer.stubFor(
            WireMock.get("/todos").willReturn(
                aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("[]")
            )
        )
        this.webTestClient.get().uri("/api/todos").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk.expectBody().jsonPath("$.length()")
            .isEqualTo(0)
    }

    @Test
    @Order(2)
    fun wireMockRequestMatching() {
        wireMockServer.stubFor(
            WireMock.get("/todos").willReturn(
                aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("[]")
            )
        )
        wireMockServer.stubFor(
            WireMock.get("/todos").willReturn(
                aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("[]")
            )
        )
        this.webTestClient.get().uri("/api/todos").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk.expectBody().jsonPath("$.length()")
            .isEqualTo(0)
    }

    @Test
    @Order(3)
    fun wireMockRequestMatchingWithPriority() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/todos")).atPriority(1).willReturn(
                aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("[]")
            )
        )
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/users")).atPriority(10).willReturn(
                aResponse().withStatus(500)
            )
        )
        this.webTestClient.get().uri("/api/todos").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().is2xxSuccessful.expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    @Order(4)
    fun wireMockRequestMatchingWithData() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/todos")).atPriority(1).willReturn(
                aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("todo-api/response-200.json")
            )
        )

        this.webTestClient.get().uri("/api/todos").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().is2xxSuccessful.expectBody()
            .jsonPath("$.length()").isEqualTo(3).jsonPath("$[0].title").isEqualTo("delectus aut autem")

        wireMockServer.verify(
            exactly(1), getRequestedFor(urlEqualTo("/todos"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("SpringBootKotlinApplication"))
        )

        val events: List<ServeEvent> = wireMockServer.allServeEvents
        events[0].request.containsHeader(HttpHeaders.USER_AGENT)

        val unMatchedRequests = wireMockServer.findAllUnmatchedRequests()

        assertEquals(0, unMatchedRequests.size)
    }
}