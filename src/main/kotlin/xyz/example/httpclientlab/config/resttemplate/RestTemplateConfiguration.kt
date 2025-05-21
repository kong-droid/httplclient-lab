package xyz.example.httpclientlab.config.resttemplate

import com.google.gson.Gson
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy
import org.apache.hc.client5.http.impl.classic.DefaultBackoffStrategy
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.util.TimeValue
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

@Configuration
class RestTemplateConfiguration(private val gson: Gson) {

    @Bean
    fun restTemplate(httpClient: HttpClient): RestTemplate {
        return RestTemplate(HttpComponentsClientHttpRequestFactory(httpClient)).apply {
            messageConverters = listOf(GsonHttpMessageConverter(gson))
        }
    }

    @Bean
    fun restClient(httpClient: HttpClient): RestClient {
        return RestClient.builder().requestFactory(HttpComponentsClientHttpRequestFactory(httpClient)).build()
    }

    @Bean
    fun httpClient(): HttpClient {
        val sslContext = sslContext()

        val connectionManager = PoolingHttpClientConnectionManagerBuilder.create().apply {
            setTlsSocketStrategy(DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE))
            setMaxConnTotal(256)
            setMaxConnPerRoute(5)
            setDefaultSocketConfig(SocketConfig.DEFAULT)
            setDefaultConnectionConfig(ConnectionConfig.DEFAULT)
        }.build()

        val requestConfig = RequestConfig.custom().apply {
            setConnectionRequestTimeout(10L, TimeUnit.SECONDS)
            setResponseTimeout(10L, TimeUnit.SECONDS)
        }.build()

        return HttpClients.custom().apply {
            setConnectionManager(connectionManager)
            setDefaultRequestConfig(requestConfig)
            setConnectionBackoffStrategy(DefaultBackoffStrategy())
            setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy())
            setRetryStrategy(DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(1)))
            evictIdleConnections(TimeValue.ofSeconds(10L))
        }.build()
    }

    private fun sslContext(): SSLContext? {
        try {
            val builder = SSLContexts.custom().apply {
                setSecureRandom(SecureRandom())
                loadTrustMaterial(null) {_, _ -> true}
            }
            builder.setProtocol(protocol())
            return builder.build()
        } catch (e: GeneralSecurityException) {
            return null
        }
    }

    private fun protocol(): String? {
        val protocols = System.getProperty("https.protocols", null) ?: return null
        val protocolsArray = protocols.split(",")
        if(protocolsArray.isEmpty()) return null
        return try {
            protocolsArray.first {
                it.lowercase().startsWith("tlsv")
            }
        } catch (e: Exception) {
            protocolsArray[0]
        }
    }

}