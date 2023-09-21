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

    @Scheduled(cron = "*/3 * * * * *")
    fun updateDns() {
        val externalIp: String = externalIpRetrieverService.getExternalIp()
        LOGGER.info("running scheduler. get external ip: $externalIp")
        if (externalIp != savedIp) {
            val ipWithfqdn = gandiClient.doUpdateIpWithfqdn(externalIp, "myfqdn")
            LOGGER.info("Updating myfqdn")
            LOGGER.info(ipWithfqdn.body)
            savedIp = externalIp
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}