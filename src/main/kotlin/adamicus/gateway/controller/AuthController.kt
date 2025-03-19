package adamicus.gateway.controller

import adamicus.gateway.model.LoginRequest
//import adamicus.gateway.service.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

//TODO update this with user service
//@RestController
//@RequestMapping("/api/auth")
//class AuthController(
//    private val tokenService: TokenService
//) {
//
//    @PostMapping("/login")
//    fun login(@RequestParam loginRequest: LoginRequest): Mono<Map<String, String>> {
//        return tokenService.generateToken(userId)
//            .map { mapOf("token" to it) }
//    }
//
//    @PostMapping("/logout")
//    fun logout(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): Mono<String> {
//        return tokenService.revokeToken(token).thenReturn("Logged out")
//    }
//}