package net.flyingelectrons.dnsupdater.service

import mu.KotlinLogging
import net.flyingelectrons.dnsupdater.gateway.GandiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UpdateDnsSchedulingService @Autowired constructor(
    private val externalIpRetrieverService: ExternalIpRetrieverService,
    private val gandiClient: GandiClient,
) {

    private var savedIp = "noIpKnownYet"

    @Scheduled(cron = "\${dns.update.cron}" )
    fun updateDns() {
        try {
            LOGGER.info("running scheduler")
            val externalIp: String = externalIpRetrieverService.getExternalIp()
            LOGGER.info("get external ip: $externalIp")

            if (externalIp != savedIp) {
                val ipWithfqdn = gandiClient.doUpdateIpWithfqdn(externalIp, "myfqdn")
                LOGGER.info("Updating {}", ipWithfqdn.body)
                savedIp = externalIp
            } else {
                LOGGER.info("IP did not change")
            }
        } catch (ex: IllegalArgumentException) {
            LOGGER.info("running scheduler failed with {}", ex.message)
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}