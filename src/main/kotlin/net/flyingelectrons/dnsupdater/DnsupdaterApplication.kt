package net.flyingelectrons.dnsupdater

import net.flyingelectrons.dnsupdater.configuration.DnsServiceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DnsServiceProperties::class)
class DnsupdaterApplication {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }
} 

fun main(args: Array<String>) {
    runApplication<DnsupdaterApplication>(*args)
}
