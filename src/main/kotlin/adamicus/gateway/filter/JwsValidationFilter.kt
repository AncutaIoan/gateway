package adamicus.gateway.filter

import adamicus.gateway.service.TokenService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class JwsValidationFilter(private val tokenService: TokenService) : AbstractGatewayFilterFactory<Nothing>() {
    override fun apply(config: Nothing?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            tokenService.extractPayloadFromAuthHeader(exchange.request)
                .flatMap { userPayload ->
                    val mutatedRequest = exchange.request.mutate()
                        .header("X-JWS-Payload", userPayload)
                        .build()
                    chain.filter(exchange.mutate().request(mutatedRequest).build())
                }
                .switchIfEmpty { exchange.response.setComplete() }
        }
    }
}