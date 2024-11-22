package net.flyingelectrons.dnsupdater.service

import mu.KotlinLogging
import net.flyingelectrons.dnsupdater.configuration.DnsServiceProperties
import net.flyingelectrons.dnsupdater.gateway.GandiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val LOGGER = KotlinLogging.logger { }

@Service
class UpdateDnsSchedulingService @Autowired constructor(
    dnsServiceProperties: DnsServiceProperties,
    private val externalIpRetrieverService: ExternalIpRetrieverService,
    private val gandiClient: GandiClient,
) {

    var addressToIpMemoryMap: MutableMap<Address, IpMemory> = mutableMapOf()

    init {
        dnsServiceProperties.websites.map { website ->
            website.subdomains.map { subdomain ->
                addressToIpMemoryMap[Address(website.url, subdomain)] = IpMemory()
            }
        }
    }

    @Scheduled(cron = "\${dnsupdater.update-cron}")
    fun updateDns() {
        addressToIpMemoryMap.entries.forEach { (address, ipMemory) ->
            try {
                LOGGER.info("running scheduler")
                val externalIp: String = externalIpRetrieverService.getExternalIp()
                LOGGER.info("get external ip: $externalIp")

                if (externalIp != ipMemory.ip) {
                    val ipWithfqdn: ResponseEntity<String> =
                        gandiClient.doUpdateIpWithSubdomain(externalIp, address.subdomain, address.domain)
                    LOGGER.info("Updating ${address.domain} ${address.subdomain}  ${ipMemory.ip}")
                    if (ipWithfqdn.statusCode == HttpStatus.CREATED) {
                        ipMemory.ip = externalIp
                    } else {
                        LOGGER.info("IP of fqdn ${address.subdomain} did not change")  //TODO: fix names
                    }
                }

            } catch (ex: IllegalArgumentException) {
                LOGGER.info("running scheduler failed with {}", ex.message)
            }
        }

    }
}

data class IpMemory(var ip: String = "noIpYet")
data class Address(val domain: String, val subdomain: String)
