package net.flyingelectrons.dnsupdater.gateway

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate

@Service
class GandiClient {

    private val restTemplate = RestTemplate()

    @Value(value = "\${dns.service.api-key}")
    private lateinit var apiKey: String

    @Value(value = "\${dns.service.api-url}")
    private lateinit var dnsApiUrl: String

    fun doUpdateIpWithfqdn(ipAddress: String, fqdn: String): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        val requestEntity = HttpEntity(
            MyRequestBody("300", listOf(ipAddress)), HttpHeaders().apply { httpHeaders["Authorization"] = "Apikey $apiKey" })
        return try {
            val responseEntity = restTemplate.exchange(
                "$dnsApiUrl$fqdn/A",
                HttpMethod.PUT,
                requestEntity,
                String::class.java
            )
            LOGGER.info(responseEntity.body.toString())
            LOGGER.info(responseEntity.headers.toString())
            responseEntity
        } catch (httpStatusCodeException: HttpStatusCodeException) {
            LOGGER.warn(httpStatusCodeException.toString())
            LOGGER.error(httpStatusCodeException.message)
            ResponseEntity<String>(httpStatusCodeException.statusCode)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(GandiClient::class.java)
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MyRequestBody(var rrsetTtl: String, val rrsetValues: List<String>)
}