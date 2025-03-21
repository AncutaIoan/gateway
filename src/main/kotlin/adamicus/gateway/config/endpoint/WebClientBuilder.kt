package adamicus.gateway.config.endpoint

import adamicus.gateway.config.endpoint.FilterFunction.Companion.logRequest
import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

object WebClientBuilder {
    private const val MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024

    fun build(endpointProperties: EndpointProperties): WebClient =
        WebClient.builder()
            .baseUrl(endpointProperties.url)
            .filter(logRequest())
            .defaultHeader(HttpHeaders.AUTHORIZATION, endpointProperties.authHeader)
            .clientConnector(endpointProperties.connectorOf)
            .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
            .build()
}


class FilterFunction {
    companion object {
        private val LOG = LoggerFactory.getLogger(FilterFunction::class.java)
        fun logRequest(): ExchangeFilterFunction {
            return ExchangeFilterFunction.ofRequestProcessor {
                clientRequest: ClientRequest ->
                    LOG.debug("Request HTTP: {} {} {}", clientRequest.method(), clientRequest.url(), clientRequest.headers())
                    Mono.just(clientRequest)
            }
        }
    }
}

private val EndpointProperties.connectorOf: ClientHttpConnector
    get() = ReactorClientHttpConnector(httpClient)

private val EndpointProperties.httpClient
    get() = HttpClient.create(ConnectionProvider.create("poolName"))
        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeoutSec * 1000)
        .doOnConnected { connection -> connection.addHandlerFirst(ReadTimeoutHandler(readTimeoutSec)).onReadIdle(idleTimeoutSec * 1000L) {} }
