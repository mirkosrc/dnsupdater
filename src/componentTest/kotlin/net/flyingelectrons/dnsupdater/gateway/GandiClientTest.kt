package net.flyingelectrons.dnsupdater.gateway

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("ct")
internal class GandiClientTest {

    @Autowired
    private lateinit var gandiClient: GandiClient

    @Test
    fun testDnsUpdater() {
        val wireMockServer = WireMockServer()
        wireMockServer.start()
        WireMockConfiguration.options().notifier(ConsoleNotifier(true))
        configureFor("localhost", 8080)
        stubFor(put(urlEqualTo("/api/v5/domains/foo.net/records/bar/A"))
            .willReturn(okJson("{\"keks\":\"dose\"}")))
        
        val ipAddress= "1.1.1.1"
        val responseCode = gandiClient.doUpdateIpWithfqdn(ipAddress, "bar")
        assertThat(responseCode.statusCode.value()).isEqualTo(200)
        wireMockServer.stop()
    }
}