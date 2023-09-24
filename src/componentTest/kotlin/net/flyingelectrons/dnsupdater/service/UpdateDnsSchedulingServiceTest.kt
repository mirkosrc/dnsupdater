package net.flyingelectrons.dnsupdater.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import net.flyingelectrons.dnsupdater.gateway.GandiClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("ct")
class UpdateDnsSchedulingServiceTest {

    @Autowired
    private lateinit var updateDnsSchedulingService: UpdateDnsSchedulingService

    @MockkBean
    private lateinit var externalIpRetrieverService: ExternalIpRetrieverService

    @MockkBean
    private lateinit var gandiClient: GandiClient


    @Test
    fun `New external IP should be saved if update call to dns provider was successful`() {
        every { externalIpRetrieverService.getExternalIp() } returns "1.1.1.1"
        every { gandiClient.doUpdateIpWithfqdn(any(), any()) } returns ResponseEntity<String>(HttpStatus.CREATED)
        updateDnsSchedulingService.updateDns()

        assertThat(updateDnsSchedulingService.ipMemoryList[0]).isEqualTo(IpMemory("foo1", "1.1.1.1"))
    }

    @Test
    fun `New external IP should not be saved if update call to dns provider fails`() {
        every { externalIpRetrieverService.getExternalIp() } returns "1.1.1.1"
        every { gandiClient.doUpdateIpWithfqdn(any(), any()) } returns ResponseEntity<String>(HttpStatus.FORBIDDEN)
        updateDnsSchedulingService.updateDns()

        assertThat(updateDnsSchedulingService.ipMemoryList[0]).isEqualTo(IpMemory("foo1", "noIpYet"))
    }

    @Test
    fun `Save no IP if ExternalIpRetrieverService fails`() {
        every { externalIpRetrieverService.getExternalIp() } throws IllegalArgumentException()
        updateDnsSchedulingService.updateDns()

        assertThat(updateDnsSchedulingService.ipMemoryList[0]).isEqualTo(IpMemory("foo1", "noIpYet"))

        verify(exactly = 0) { gandiClient.doUpdateIpWithfqdn(any(), any()) }
    }
}