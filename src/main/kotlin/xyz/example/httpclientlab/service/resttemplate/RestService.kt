package xyz.example.httpclientlab.service.resttemplate

import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException

interface RestService {
    @Throws(RestClientException::class)
    fun <T> get(host: String, path: String, params: MultiValueMap<String, String>?, responseType: Class<T>,
                extras: Map<String, Any>?): ResponseEntity<T>

    @Throws(RestClientException::class)
    fun <T, R: Any> post(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                         responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T>

    @Throws(RestClientException::class)
    fun <T, R: Any> put(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                         responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T>

    @Throws(RestClientException::class)
    fun <T, R: Any> patch(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                         responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T>

    @Throws(RestClientException::class)
    fun <T, R: Any> delete(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                         responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T>

}