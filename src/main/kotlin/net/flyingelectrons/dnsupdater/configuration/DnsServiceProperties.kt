package net.flyingelectrons.dnsupdater.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dnsupdater")
data class DnsServiceProperties(
    var websites: List<Website> = listOf()
)

data class Website(
    var url: String = "",
    var subdomains: List<String> = listOf()
)