package adamicus.gateway.service

import adamicus.gateway.config.JwsConfig
import adamicus.gateway.config.ObjectMapperConfiguration
import adamicus.gateway.model.UserPayload
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import java.time.Duration

class TokenServiceTest {

    private val objectMapper = ObjectMapperConfiguration().objectMapper()
    private val redisTemplate =  mock<ReactiveStringRedisTemplate>()

    private val jwsConfig = JwsConfig(
        signingKey = "9e7d38b3-00ac-4357-9c24-4ddeb77c1ab8",
        encryptionKey = "161397bf-8ee9-4bc8-a2bf-024a99a8238f",
        encryptionSalt = "5CD634A6759F88C4",
        audience = "16cc4cc8-a865-4caa-8dba-cb9b7aacdbdd",
        issuer = "gateway",
        allowedClockSkew = Duration.ofSeconds(60),
        expireAfter = Duration.ofDays(30),
        prefix = "cool-prefix"
    )



    private val tokenService: TokenService = TokenService(objectMapper, jwsConfig, redisTemplate)

    @Test
    fun generateToken_validLoginResponse_tokenGenerated() {
        val userPayload = UserPayload(1L)

        val token = tokenService.generateToken(userPayload).block()
        assertThat(token)
            .isNotNull()
            .isNotEmpty()
    }

    @Test
    fun toPayload_loginResponseReturned() {
        val userPayload = UserPayload(1L)
        val token = tokenService.generateToken(userPayload).block()

        val validatedResponse = tokenService.toPayload(token!!).block()

        assertThat(validatedResponse).isNotNull
        assertThat(validatedResponse?.userId).isEqualTo(1L)
    }
}
