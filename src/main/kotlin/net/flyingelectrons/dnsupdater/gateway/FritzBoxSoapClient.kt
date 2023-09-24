package net.flyingelectrons.dnsupdater.gateway

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

@Service
class FritzBoxSoapClient(private val restTemplate: RestTemplate) {

    fun getExternalIPAddress(): String {
        return extractIPAddressFromSoapResponse(getSoapResponseWithExternalIPAddress())
    }

    private fun getSoapResponseWithExternalIPAddress(): String {
        val soapEnvelope = """
            <?xml version='1.0' encoding='utf-8'?>
            <s:Envelope s:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'>
                <s:Body>
                    <u:GetExternalIPAddress xmlns:u='urn:schemas-upnp-org:service:WANIPConnection:1' />
                </s:Body>
            </s:Envelope>
        """.trimIndent()

        val headers = HttpHeaders()
        headers.contentType = MediaType("text", "xml", Charsets.UTF_8)
        headers.add("SoapAction", "urn:schemas-upnp-org:service:WANIPConnection:1#GetExternalIPAddress")

        val requestEntity = RequestEntity
            .post(URI("http://fritz.box:49000/igdupnp/control/WANIPConn1"))
            .headers(headers)
            .body(soapEnvelope)

        val responseEntity: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)

        if (responseEntity.statusCode.is2xxSuccessful) {
            return responseEntity.body ?: ""
        } else {
            throw RuntimeException("SOAP request failed with status code: ${responseEntity.statusCode}")
        }
    }

    private fun extractIPAddressFromSoapResponse(response: String?): String {
        if (response == null) {
            throw RuntimeException("SOAP response is empty")
        }

        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val input = ByteArrayInputStream(response.toByteArray())
        val document: Document = documentBuilder.parse(input)

        val ipAddressElement = document.getElementsByTagName("NewExternalIPAddress").item(0)

        if (ipAddressElement != null) {
            return ipAddressElement.textContent
        } else {
            throw RuntimeException("Could not extract IP address from SOAP response")
        }
    }
}