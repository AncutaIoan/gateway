package adamicus.gateway.service

import adamicus.gateway.model.LoginResponse
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

//TODO use identity logic for jwt util, extra logic to props for identity
//@Service
//class TokenService(
//    private val redisTemplate: ReactiveRedisTemplate<String, String>
//) {
//    fun generateToken(user: LoginResponse): String {
//        return JWTUtil.createToken(user.userName)
//    }
//
//    fun validateToken(token: String): Mono<Boolean> {
//        return Mono.just(JWTUtil.validateToken(token))
//    }
//
//    fun getUserIdFromToken(token: String): Mono<String> {
//        return Mono.just(JWTUtil.getSubject(token))
//    }
//
//    fun revokeToken(userId: String, token: String): Mono<ResponseEntity<Void>> {
//        return redisTemplate.opsForValue().set("revokedToken:$userId", token)
//            .flatMap {
//                Mono.just(ResponseEntity.noContent().build())
//            }
//    }
//
//    fun isTokenRevoked(userId: String, token: String): Mono<Boolean> {
//        return redisTemplate.opsForValue().get("revokedToken:$userId")
//            .map { revokedToken -> revokedToken != null && revokedToken == token }
//            .defaultIfEmpty(false)
//    }
//}