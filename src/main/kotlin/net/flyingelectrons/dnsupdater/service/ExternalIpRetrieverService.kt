package net.flyingelectrons.dnsupdater.service

import net.flyingelectrons.dnsupdater.gateway.FritzBoxSoapClient
import org.springframework.stereotype.Service

@Service
class ExternalIpRetrieverService(private val fritzBoxSoapClient: FritzBoxSoapClient) {

    fun getExternalIp(): String {
        val externalIpAddress = fritzBoxSoapClient.getExternalIPAddress()
        if (externalIpAddress.isEmpty()) {
            throw java.lang.IllegalArgumentException("external IP is empty")
        }
        return externalIpAddress
    }
}