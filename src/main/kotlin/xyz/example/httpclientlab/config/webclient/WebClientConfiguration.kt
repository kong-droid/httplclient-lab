package xyz.example.httpclientlab.config.webclient

import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.time.Duration

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClientBuilder(): WebClient.Builder =
        WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(ReactorClientHttpConnector(httpClient()))
            .codecs {
                it.defaultCodecs().maxInMemorySize(15 * 1024 * 1024) // 15MB
            }


    private fun httpClient(): HttpClient {
        val connectProvider = ConnectionProvider.builder("httpclient-lab")
            .maxConnections(256)
            .build()

        return HttpClient.create(connectProvider)
            .secure { spec ->
                sslContext()?.let {
                    ssl -> spec.sslContext(ssl)
                }
            }
            .responseTimeout(Duration.ofSeconds(10))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .doOnConnected {
                it.addHandlerLast(ReadTimeoutHandler(5))
                it.addHandlerLast(WriteTimeoutHandler(5))
            }
    }

    private fun sslContext(): SslContext? =
        try {
            SslContextBuilder.forClient()
                .secureRandom(SecureRandom())
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .protocols(protocol())
                .build()
        } catch (e: GeneralSecurityException) {
            null
        }

    private fun protocol(): String? =
        System.getProperty("https.protocols")
            ?.split(",")
            ?.map { it.trim() }
            ?.firstOrNull { it.lowercase().startsWith("tlsv")}
            ?: "TLSv1.3" // java17 default
}