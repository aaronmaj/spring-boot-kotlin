package com.spring.blog

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent

class WiremockInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()

        applicationContext.addApplicationListener() {
            if (it is ContextClosedEvent) {
                wireMockServer.stop()
            }
        }
        applicationContext.beanFactory.registerSingleton("wireMockServer", wireMockServer)
        //val map: Map<String,String> = mapOf("todo_base_url" to wireMockServer.url("").toString());
        // val filteredMap = map.filter { (key, value) -> key.endsWith("1") && value > 10}

        TestPropertyValues.of(mapOf("todo_base_url" to wireMockServer.url("").toString()))
            .applyTo(applicationContext)

    }
}