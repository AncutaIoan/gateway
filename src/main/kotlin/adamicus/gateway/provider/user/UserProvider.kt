package adamicus.gateway.provider.user

import adamicus.gateway.model.LoginRequest
import adamicus.gateway.model.UserPayload
import reactor.core.publisher.Mono

interface UserProvider {
    fun authenticate(loginRequest: LoginRequest): Mono<UserPayload>
}