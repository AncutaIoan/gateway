package adamicus.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "jws")
data class JwsConfig(
    val signingKey: String,
    val encryptionKey: String,
    val encryptionSalt: String,
    val audience: String,
    val issuer: String,
    val allowedClockSkew: Duration,
    val expireAfter: Duration
)
