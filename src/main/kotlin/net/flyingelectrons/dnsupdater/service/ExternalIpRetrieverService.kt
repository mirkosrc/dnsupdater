package net.flyingelectrons.dnsupdater.service

import org.springframework.stereotype.Service

@Service
class ExternalIpRetrieverService(private val processExecutorService: ProcessExecutorService) {

    fun getExternalIp(): String {
        val execute = processExecutorService.execute("external-ip")
        if (execute.isEmpty()) {
            throw java.lang.IllegalArgumentException("external IP is empty")
        }
        return execute
    }
}