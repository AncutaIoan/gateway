package adamicus.gateway.config

import java.util.*

interface EndpointConfig {
    val url: String
    val user: String?
    val password: String?
    val poolMaxSize: Int
    val connectionTimeoutSec: Int
    val readTimeoutSec: Int
    val idleTimeoutSec: Int
    val authMode: SupportedAuthMode
    val authHeader: String?
        get() = when (authMode) {
                    SupportedAuthMode.BASIC -> "Basic ${b64Encoder(user!!, password)}"
                    SupportedAuthMode.BASIC_SIMPLE -> "Basic $user $password"
                    SupportedAuthMode.BEARER -> "Bearer $user"
                    SupportedAuthMode.APP_KEY -> user
                    else -> null
                }

    fun b64Encoder(user: String, password: String?): String =
        Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    enum class SupportedAuthMode {
        BASIC, BASIC_SIMPLE, BEARER, APP_KEY, NONE
    }
}