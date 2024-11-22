package net.flyingelectrons.dnsupdater.configuration

import net.flyingelectrons.dnsupdater.service.Address
import net.flyingelectrons.dnsupdater.service.IpMemory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("ct")
class DnsServicePropertiesTest {

    @Autowired
    private lateinit var dnsServiceProperties: DnsServiceProperties

    @Test
    fun `test property mapping from yaml file`() {
        assertThat(dnsServiceProperties.websites.size).isGreaterThan(0)
        println(dnsServiceProperties.websites)
    }

    @Test
    fun `test change to map`() {
        val addressToIpMemoryMap: MutableMap<Address, IpMemory> = mutableMapOf()

        dnsServiceProperties.websites.map { website ->
            website.subdomains.map { subdomain ->
                addressToIpMemoryMap[Address(website.url, subdomain)] = IpMemory()
            }
        }
        assertThat(addressToIpMemoryMap.size).isGreaterThan(0)
        println(addressToIpMemoryMap.toString())
    }

}