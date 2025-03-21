package adamicus.gateway.provider.user.config

import adamicus.gateway.config.endpoint.WebClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserConfiguration {
    @Bean
    fun userWebClient(userEndpointProperties: UserEndpointProperties) = WebClientBuilder.build(userEndpointProperties)
}