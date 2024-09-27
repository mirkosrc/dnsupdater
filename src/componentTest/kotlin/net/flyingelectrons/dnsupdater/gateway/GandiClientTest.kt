package net.flyingelectrons.dnsupdater.gateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest
@ActiveProfiles("ct")
internal class GandiClientTest {

    @Autowired
    private lateinit var gandiClient: GandiClient
    
    @Autowired
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockRestServiceServer: MockRestServiceServer

    @BeforeEach
    fun init() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `gandi client should return 201 response code from server in case of successful ip update`() {
        mockRestServiceServer.expect(ExpectedCount.once(),
            requestTo( URI("http://localhost:8080/api/v5/domains/domain1.net/records/fqdn/A")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"keks\":\"dose\"}")
            );
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithSubdomain(ipAddress, "fqdn")
        assertThat(responseCode.statusCode.value()).isEqualTo(201)
    }

    @Test
    fun `gandi client for second domain should return 201 response code from server in case of successful ip update`() {
        mockRestServiceServer.expect(ExpectedCount.once(),
            requestTo( URI("http://localhost:8080/api/v5/domains/domain2.net/records/fqdn/A")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"keks\":\"dose\"}")
            )
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithSubdomain2(ipAddress, "fqdn")
        assertThat(responseCode.statusCode.value()).isEqualTo(201)
    }

    @Test
    fun `gandi client should return 403 in case of FORBIDDEN response`() {
        mockRestServiceServer.expect(ExpectedCount.once(),
            requestTo( URI("http://localhost:8080/api/v5/domains/domain1.net/records/subdomain/A")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"keks\":\"fehler\"}")
            )
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithSubdomain(ipAddress, "subdomain")
        assertThat(responseCode.statusCode.value()).isEqualTo(403)
    }
}