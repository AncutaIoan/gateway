package adamicus.gateway.service

import adamicus.gateway.model.LoginRequest
import adamicus.gateway.provider.user.UserProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(
    private val tokenService: TokenService,
    private val userProvider: UserProvider
) {
    fun login(loginRequest: LoginRequest): Mono<ResponseEntity<Map<String, String>>> =
        userProvider.authenticate(loginRequest)
            .flatMap { user -> tokenService.generateToken(user) }
            .map { token -> ResponseEntity.ok(mapOf("token" to token)) }
            .onErrorResume { Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Authentication failed"))) }
}