package adamicus.gateway.provider.user.config

import adamicus.gateway.config.endpoint.EndpointProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api.user")
data class UserEndpointProperties(
    override val url: String,
    override val user: String?,
    override val password: String?,
    override val poolMaxSize: Int,
    override val connectionTimeoutSec: Int,
    override val readTimeoutSec: Int,
    override val idleTimeoutSec: Int,
    override val authMode: EndpointProperties.SupportedAuthMode
) : EndpointProperties