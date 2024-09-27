package net.flyingelectrons.dnsupdater.gateway

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate

private val LOGGER = KotlinLogging.logger { }

@Service
class GandiClient @Autowired constructor(var restTemplate: RestTemplate) {


    @Value(value = "\${dns.service.api-key}")
    private lateinit var apiKey: String

    @Value(value = "\${dns.service.api-url}")
    private lateinit var dnsApiUrl: String
    
    @Value(value = "\${dns.service.api-url2}")
    private lateinit var dnsApiUrl2: String

    fun doUpdateIpWithSubdomain(ipAddress: String, subdomain: String): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders["Authorization"] = "Bearer $apiKey"
        val requestEntity = HttpEntity(MyRequestBody("300", listOf(ipAddress)), httpHeaders)
        return try {
            val responseEntity = restTemplate.exchange(
                "$dnsApiUrl$subdomain/A",
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

    fun doUpdateIpWithSubdomain2(ipAddress: String, subdomain: String): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders["Authorization"] = "Bearer $apiKey"
        val requestEntity = HttpEntity(MyRequestBody("300", listOf(ipAddress)), httpHeaders)
        return try {
            val responseEntity = restTemplate.exchange(
                "$dnsApiUrl2$subdomain/A",
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

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class MyRequestBody(var rrsetTtl: String, val rrsetValues: List<String>)
}