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
    fun testDnsUpdater() {
        mockRestServiceServer.expect(ExpectedCount.once(),
            requestTo( URI("http://localhost:8080/api/v5/domains/foo.net/records/fqdn/A")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"keks\":\"dose\"}")
            );
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithfqdn(ipAddress, "fqdn")
        assertThat(responseCode.statusCode.value()).isEqualTo(200)
    }

    @Test
    fun testDnsUpdater2() {
        mockRestServiceServer.expect(ExpectedCount.once(),
            requestTo( URI("http://localhost:8080/api/v5/domains/foo.net/records/fqdn/A")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"keks\":\"fehler\"}")
            )
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithfqdn(ipAddress, "fqdn")
        assertThat(responseCode.statusCode.value()).isEqualTo(403)
    }
}