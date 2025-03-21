package adamicus.gateway.provider.user

import adamicus.gateway.model.LoginRequest
import adamicus.gateway.model.UserPayload
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class UserProviderImpl(private val userWebClient: WebClient) : UserProvider {
    override fun authenticate(loginRequest: LoginRequest): Mono<UserPayload> =
        userWebClient.post()
            .uri("/authenticate")
            .bodyValue(loginRequest)
            .retrieve()
            .onStatus(HttpStatusCode::isError, ::handleError)
            .bodyToMono(UserPayload::class.java)


    private fun handleError(clientResponse: ClientResponse): Mono<Throwable> =
        clientResponse.bodyToMono(String::class.java)
            .flatMap { Mono.error(RuntimeException("Error encountered during authenticate request, status = ${clientResponse.statusCode()}, response body = ${it}")) }
}