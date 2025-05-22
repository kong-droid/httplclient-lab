package xyz.example.httpclientlab.service.resttemplate

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

class RestServiceImpl(private val restTemplate: RestTemplate): RestService {
    override fun <T> get(host: String, path: String, params: MultiValueMap<String, String>?, responseType: Class<T>,
                         extras: Map<String, Any>?): ResponseEntity<T> =
        request(HttpMethod.GET, host, path, params, null, responseType, extras)

    override fun <T, R : Any> post(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                                   responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T> =
        request(HttpMethod.POST, host, path, params, body, responseType, extras)

    override fun <T, R : Any> put(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                                  responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T> =
        request(HttpMethod.PUT, host, path, params, body, responseType, extras)

    override fun <T, R : Any> patch(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                                    responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T> =
        request(HttpMethod.PATCH, host, path, params, body, responseType, extras)

    override fun <T, R : Any> delete(host: String, path: String, params: MultiValueMap<String, String>?, body: R?,
                                     responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T> =
        request(HttpMethod.DELETE, host, path, params, body, responseType, extras)

    private fun <T> call(host: String, path: String, params: MultiValueMap<String, String>?,
                         caller: (RestTemplate, String) -> T): T {
        val scheme = if(host.contains("://")) host.split("://").first() else "http"
        val fullHost = if(host.contains("://")) host.split("://").last() else host
        val realHost = if(fullHost.contains(":")) fullHost.split(":").first() else fullHost
        val realPort = if(fullHost.contains(":")) fullHost.split(":").last().toInt() else 80
        val uriComponent = UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(realHost)
            .port(realPort)
            .path(path)
            .queryParams(params)
        println("RestService call: ${uriComponent.toUriString()}")
        return caller(restTemplate, uriComponent.toUriString())
    }

    private fun <T, R: Any> request(method: HttpMethod, host: String, path: String, params: MultiValueMap<String, String>?,
                                    body: R?, responseType: Class<T>, extras: Map<String, Any>?): ResponseEntity<T> =
        call(host, path, params) { restTemplate, uri ->
            val requestEntity = body?.let {
                HttpEntity(it)
            }
            val response = restTemplate.exchange(uri, method, requestEntity, responseType)
            return@call response
        }
}