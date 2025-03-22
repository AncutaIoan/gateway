package adamicus.gateway.filter

import adamicus.gateway.model.UserPayload
import adamicus.gateway.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty


@Component
class JwsValidationFilter(private val tokenService: TokenService) : AbstractGatewayFilterFactory<JwsValidationFilter.Config>(Config::class.java) {

    companion object {
        private val log = LoggerFactory.getLogger(JwsValidationFilter::class.java)
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            tokenService.extractPayloadFromAuthHeader(exchange.request)
                .switchIfEmpty { Mono.error(RuntimeException("Auth header not found")) }
                .map { userPayload -> enrichRequest(exchange, userPayload) }
                .flatMap { chain.filter(exchange.mutate().request(it).build()) }
                .onErrorResume { error ->
                    log.error("Encountered an error while validating the payload", error)
                    exchange.response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    exchange.response.setComplete()
                }
        }
    }

    private fun enrichRequest(exchange: ServerWebExchange, userPayload: UserPayload): ServerHttpRequest {
        return exchange.request.mutate()
            .header("X-JWS-Payload", userPayload.userId.toString())
            .build()
    }

    data class Config(
        val headerName: String = "Authorization"
    )
}