package xyz.example.httpclientlab.service.webclient

import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import kotlin.jvm.Throws

interface WebClientService {
    @Throws(RestClientException::class)
    fun get(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any?,
            responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>?
    @Throws(RestClientException::class)
    fun post(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
             responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>?
    @Throws(RestClientException::class)
    fun put(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
            responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>?
    @Throws(RestClientException::class)
    fun patch(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
              responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>?
    @Throws(RestClientException::class)
    fun delete(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any?,
             responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>?
}