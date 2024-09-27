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

    @Autowired
    private lateinit var externalIpRetrieverService: ExternalIpRetrieverService

    @Autowired
    private lateinit var gandiClient: GandiClient

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
        val domainsToSubdomainsList = mapOf(
            "domain1.net" to listOf("subdomain1", "subdomain11", "subdomain111"),
            "domain2.net" to listOf("subdomain2", "subdomain22", "subdomain222"),
        )

        domainsToSubdomainsList.flatMap { (domain, subdomains) ->
            subdomains.map { subdomain ->
                mockRestServiceServer.asserRequestTo(domain, subdomain)
            }
        }

        every { fritzBoxSoapClient.getExternalIPAddress() } returns "1.2.3.4"

        updateDnsSchedulingService.updateDns()

        mockRestServiceServer.verify()
    }


    private fun MockRestServiceServer.asserRequestTo(domain: String, subdomain: String) {
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
