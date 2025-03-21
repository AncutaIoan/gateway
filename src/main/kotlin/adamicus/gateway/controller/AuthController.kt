package adamicus.gateway.controller

import adamicus.gateway.model.LoginRequest
import adamicus.gateway.model.UserPayload
import adamicus.gateway.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/auth")
class AuthController(
    private val tokenService: TokenService,
    private val webClientBuilder: WebClient.Builder
) {

    private val webClient: WebClient = webClientBuilder.baseUrl("http://user-service").build()

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val userPayload = webClient.post()
            .uri("/authenticated")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(UserPayload::class.java)
            .block()

        if (userPayload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val token = tokenService.generateToken(userPayload)
        return ResponseEntity.ok(TokenResponse(token))
    }
}

data class TokenResponse(val token: String)
