package net.flyingelectrons.dnsupdater.service

import mu.KotlinLogging
import net.flyingelectrons.dnsupdater.gateway.GandiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UpdateDnsSchedulingService @Autowired constructor(
    private val externalIpRetrieverService: ExternalIpRetrieverService,
    private val gandiClient: GandiClient,
    @Value(value = "\${subdomain.names}")
    private var subdomains: List<String>,
    @Value(value = "\${subdomain.names2}")
    private var subdomains2: List<String>
) {

    var ipMemoryList: List<IpMemory> = subdomains.map(::IpMemory)
    var ipMemoryList2: List<IpMemory> = subdomains2.map(::IpMemory)
    

    @Scheduled(cron = "\${dns.update.cron}" )
    fun updateDns() {
        try {
            LOGGER.info("running scheduler")
            val externalIp: String = externalIpRetrieverService.getExternalIp()
            LOGGER.info("get external ip: $externalIp")

            for (ipMemory in ipMemoryList) {
                if (externalIp != ipMemory.ip) {
                    val ipWithfqdn: ResponseEntity<String> = gandiClient.doUpdateIpWithSubdomain(externalIp, ipMemory.subdomain)
                    LOGGER.info("Updating ${ipMemory.subdomain}")
                    if (ipWithfqdn.statusCode == HttpStatus.CREATED) {
                        ipMemory.ip = externalIp
                    } else {
                        LOGGER.info("IP of fqdn ${ipMemory.subdomain} did not change")
                    }
                }
            }

            for (ipMemory in ipMemoryList2) {
                if (externalIp != ipMemory.ip) {
                    val ipWithfqdn: ResponseEntity<String> = gandiClient.doUpdateIpWithSubdomain2(externalIp, ipMemory.subdomain)
                    LOGGER.info("Updating2 ${ipMemory.subdomain}")
                    if (ipWithfqdn.statusCode == HttpStatus.CREATED) {
                        ipMemory.ip = externalIp
                    } else {
                        LOGGER.info("IP of fqdn2 ${ipMemory.subdomain} did not change")
                    }
                }
            }
        } catch (ex: IllegalArgumentException) {
            LOGGER.info("running scheduler failed with {}", ex.message)
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}

data class IpMemory(var subdomain: String, var ip:String = "noIpYet")