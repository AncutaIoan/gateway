package adamicus.gateway.controller

import adamicus.gateway.model.LoginRequest
import adamicus.gateway.model.UserPayload
import adamicus.gateway.service.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val tokenService: TokenService,
) {


    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): Mono<ResponseEntity<TokenResponse>> {
        return tokenService.generateToken(UserPayload(123))
                .map { ResponseEntity.ok(TokenResponse(it)) }
    }
}

data class TokenResponse(val token: String)
