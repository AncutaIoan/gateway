package adamicus.gateway.service

import adamicus.gateway.config.JwsConfig
import adamicus.gateway.model.LoginResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

class TokenServiceTest {

    private val objectMapper = ObjectMapper()

    private val jwsConfig = JwsConfig(
        signingKey = "9e7d38b3-00ac-4357-9c24-4ddeb77c1ab8",
        encryptionKey = "161397bf-8ee9-4bc8-a2bf-024a99a8238f",
        encryptionSalt = "5CD634A6759F88C4",
        audience = "16cc4cc8-a865-4caa-8dba-cb9b7aacdbdd",
        issuer = "gateway",
        allowedClockSkew = Duration.ofSeconds(60),
        expireAfter = Duration.ofDays(30)
    )

    private val tokenService: TokenService = TokenService(objectMapper, jwsConfig)

    @Test
    fun generateToken_validLoginResponse_tokenGenerated() {
        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")

        val token = tokenService.generateToken(loginResponse)
        assertThat(token).isNotNull().isNotEmpty()
    }

    @Test
    fun validateToken_validToken_loginResponseReturned() {
        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")
        val token = tokenService.generateToken(loginResponse)

        val validatedResponse = tokenService.validateToken(token)

        assertThat(validatedResponse).isNotNull
        assertThat(validatedResponse?.userId).isEqualTo(1L)
        assertThat(validatedResponse?.userName).isEqualTo("user123")
        assertThat(validatedResponse?.email).isEqualTo("user123@example.com")
    }

    @Test
    fun validateToken_invalidToken_nullReturned() {
        val invalidToken = "invalid.token.here"

        val validatedResponse = tokenService.validateToken(invalidToken)

        assertThat(validatedResponse).isNull()
    }

    @Test
    fun validateToken_expiredToken_nullReturned() {
        val expiredConfig = jwsConfig.copy(expireAfter = Duration.ofSeconds(-1))
        val expiredTokenService = TokenService(objectMapper, expiredConfig)

        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")
        val token = expiredTokenService.generateToken(loginResponse)

        val validatedResponse = expiredTokenService.validateToken(token)

        assertThat(validatedResponse).isNull()
    }

    @Test
    fun validateToken_wrongAudience_nullReturned() {
        val wrongAudienceConfig = jwsConfig.copy(audience = "wrong-audience")
        val wrongAudienceTokenService = TokenService(objectMapper, wrongAudienceConfig)

        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")
        val token = tokenService.generateToken(loginResponse)

        val validatedResponse = wrongAudienceTokenService.validateToken(token)

        assertThat(validatedResponse).isNull()
    }

    @Test
    fun validateToken_wrongIssuer_nullReturned() {
        val wrongIssuerConfig = jwsConfig.copy(issuer = "gateway")
        val wrongIssuerTokenService = TokenService(objectMapper, wrongIssuerConfig)

        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")
        val token = tokenService.generateToken(loginResponse)

        val validatedResponse = wrongIssuerTokenService.validateToken(token)

        assertThat(validatedResponse).isNull()
    }

    @Test
    fun validateToken_modifiedToken_nullReturned() {
        val loginResponse = LoginResponse(1L, "user123", "user123@example.com")
        val token = tokenService.generateToken(loginResponse)

        val modifiedToken = token.replace(".", "a", true)

        val validatedResponse = tokenService.validateToken(modifiedToken)

        assertThat(validatedResponse).isNull()
    }
}
