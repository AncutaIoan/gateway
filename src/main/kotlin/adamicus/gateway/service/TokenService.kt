package adamicus.gateway.service

import adamicus.gateway.config.jws.JwsConfig
import adamicus.gateway.model.UserPayload
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Service
import java.time.Instant

import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.Date
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class TokenService(
    private val objectMapper: ObjectMapper,
    private val jwsConfig: JwsConfig,
    private val redisTemplate: ReactiveStringRedisTemplate
) {
    companion object {
        private const val PAYLOAD_CLAIM = "payload"
    }

    fun generateToken(userPayload: UserPayload): Mono<String> {
        val now = now()
        val expiration = now.plus(jwsConfig.expireAfter)
        val payload = objectMapper.writeValueAsString(userPayload)

        return Jwts.builder()
            .setAudience(jwsConfig.audience)
            .setIssuer(jwsConfig.issuer)
            .setIssuedAt(Date.from(now))
            .addClaims(mutableMapOf<String, Any>(PAYLOAD_CLAIM to textEncryptor().encrypt(payload)))
            .setExpiration(Date.from(expiration))
            .signWith(SignatureAlgorithm.HS256, jwsConfig.signingKey)
            .compact()
            .toMono()
            .flatMap { storeTokenInRedis(it, expiration) }
    }

    private fun storeTokenInRedis(token: String, expiration: Instant) =
        redisTemplate.opsForValue().set("${jwsConfig.prefix}$token", token, expiration.toEpochMilli())
            .thenReturn(token)

    fun extractPayloadFromAuthHeader(request: ServerHttpRequest): Mono<String> {
        return Mono.justOrEmpty(extractBearer(request)).flatMap { extractPayloadClaim(it) }
    }

    private fun extractBearer(request: ServerHttpRequest) =
        request.headers.getFirst("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")

    fun toPayload(token: String): Mono<UserPayload> =
        extractPayloadClaim(token)
            .map { objectMapper.readValue(textEncryptor().decrypt(it), UserPayload::class.java) }

    private fun extractPayloadClaim(token: String): Mono<String> =
        Jwts.parser()
            .setAllowedClockSkewSeconds(jwsConfig.allowedClockSkew.seconds)
            .requireAudience(jwsConfig.audience)
            .requireIssuer(jwsConfig.issuer)
            .setSigningKey(jwsConfig.signingKey)
            .parseClaimsJws(token)
            .toMono()
            .mapNotNull { it.body[PAYLOAD_CLAIM] as? String }


    fun revokeToken(token: String): Mono<Long> {
        return redisTemplate.delete(jwsConfig.prefix + token)
    }

    internal fun now(): Instant = Instant.now()

    private fun textEncryptor(): TextEncryptor = Encryptors.text(jwsConfig.encryptionKey, jwsConfig.encryptionSalt)
}
