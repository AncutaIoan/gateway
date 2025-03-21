package adamicus.gateway.service

import adamicus.gateway.config.JwsConfig
import adamicus.gateway.model.UserPayload
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class TokenService(private val objectMapper: ObjectMapper, private val jwsConfig: JwsConfig) {
    companion object {
        private const val PAYLOAD_CLAIM = "payload"
    }

    fun generateToken(userPayload: UserPayload): String {
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
    }

    fun toPayload(token: String): UserPayload? {
        val encryptedPayload = Jwts.parser()
            .setAllowedClockSkewSeconds(jwsConfig.allowedClockSkew.seconds)
            .requireAudience(jwsConfig.audience)
            .requireIssuer(jwsConfig.issuer)
            .setSigningKey(jwsConfig.signingKey)
            .parseClaimsJws(token)
            .body[PAYLOAD_CLAIM] as? String

        val payload  = objectMapper.readValue(textEncryptor().decrypt(encryptedPayload), UserPayload::class.java)

        return payload
    }

    private fun textEncryptor(): TextEncryptor = Encryptors.text(jwsConfig.encryptionKey, jwsConfig.encryptionSalt)

    internal fun now(): Instant = Instant.now()
}
