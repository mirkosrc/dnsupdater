package net.flyingelectrons.dnsupdater

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import net.flyingelectrons.dnsupdater.gateway.FritzBoxSoapClient
import net.flyingelectrons.dnsupdater.gateway.GandiClient
import net.flyingelectrons.dnsupdater.service.ExternalIpRetrieverService
import net.flyingelectrons.dnsupdater.service.UpdateDnsSchedulingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest
@ActiveProfiles("ct")
class CombinedTest {

    @Autowired
    private lateinit var updateDnsSchedulingService: UpdateDnsSchedulingService

    @MockkBean
    private lateinit var fritzBoxSoapClient: FritzBoxSoapClient

    private lateinit var mockRestServiceServer: MockRestServiceServer

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun init() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `scheduler runs and gandi client updates the ip`() {
        mockRestServiceServer.assertRequestTo("domain1.net", "subdomain1")
        mockRestServiceServer.assertRequestTo("domain1.net", "subdomain11")
        mockRestServiceServer.assertRequestTo("domain1.net", "subdomain111")
        mockRestServiceServer.assertRequestTo("domain2.net", "subdomain2")
        mockRestServiceServer.assertRequestTo("domain2.net", "subdomain22")
        mockRestServiceServer.assertRequestTo("domain2.net", "subdomain222")

        every { fritzBoxSoapClient.getExternalIPAddress() } returns "1.2.3.4"

        updateDnsSchedulingService.updateDns()

        mockRestServiceServer.verify()
    }

    private fun MockRestServiceServer.assertRequestTo(domain: String, subdomain: String) {
        this.expect(
            requestTo(URI("http://localhost:8080/api/v5/domains/$domain/records/$subdomain/A"))
        )
            .andExpect(method(HttpMethod.PUT))
            .andRespond(
                withStatus(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"keks\":\"dose\"}")
            )
    }
}
