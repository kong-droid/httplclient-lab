package xyz.example.httpclientlab.service.webclient

import com.google.gson.Gson
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class WebClientServiceImpl(private val gson: Gson, private val webClientBuilder: WebClient.Builder): WebClientService {
    override fun get(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any?,
                     responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>? {
        val apiPath = uri(host, path, params)
        return webClientBuilder.build()
            .get()
            .uri(apiPath)
            .retrieve()
            .toEntity(String::class.java)
            .doOnError {
                println("Error! WebClient GET($apiPath) ::: message : ${it.message}")
            }
            .onErrorResume { Mono.empty() }
            .block()
    }

    override fun post(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
                      responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>? {
        val apiPath = uri(host, path, params)
        return webClientBuilder.build()
            .post()
            .uri(apiPath)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertToJson(requestBody))
            .retrieve()
            .toEntity(String::class.java)
            .doOnError {
                println("Error! WebClient POST($apiPath) ::: message : ${it.message}")
            }
            .onErrorResume { Mono.empty() }
            .block()
    }

    override fun put(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
            responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>? {
        val apiPath = uri(host, path, params)
        return webClientBuilder.build()
            .put()
            .uri(uri(host, path, null))
            .bodyValue(convertToJson(requestBody))
            .retrieve()
            .toEntity(String::class.java)
            .doOnError {
                println("Error! WebClient PUT($apiPath) ::: message : ${it.message}")
            }
            .onErrorResume { Mono.empty() }
            .block()
    }

    override fun patch(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any,
              responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>? {
        val apiPath = uri(host, path, params)
        return webClientBuilder.build()
            .patch()
            .uri(uri(host, path, null))
            .bodyValue(convertToJson(requestBody))
            .retrieve()
            .toEntity(String::class.java)
            .doOnError {
                println("Error! WebClient PATCH($apiPath) ::: message : ${it.message}")
            }
            .onErrorResume { Mono.empty() }
            .block()
    }

    override fun delete(host: String, path: String, params: MultiValueMap<String, String>?, requestBody: Any?,
               responseType: Any, extras: Map<String, Any>?): ResponseEntity<String>? {
        val apiPath = uri(host, path, params)
        return webClientBuilder.build()
            .delete()
            .uri(apiPath)
            .retrieve()
            .toEntity(String::class.java)
            .doOnError {
                println("Error! WebClient DELETE($apiPath) ::: message : ${it.message}")
            }
            .onErrorResume { Mono.empty() }
            .block()
    }

    private fun uri(host: String, path: String, params: MultiValueMap<String, String>?): String {
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

        println("WebClient Call : ${uriComponent.toUriString()}")
        return uriComponent.toUriString()
    }

    private fun convertToJson(requestBody: Any): String = gson.toJson(requestBody)
}