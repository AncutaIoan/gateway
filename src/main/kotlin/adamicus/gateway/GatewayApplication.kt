package adamicus.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class GatewayApplication

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}

@Configuration
class GatewayConfig {

    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route { p ->
                p.path("/get")
                    .filters { f -> f.addRequestHeader("Hello", "World") }
                    .uri("http://httpbin.org:80")
            }
            .route { p ->
                p.host("*.circuitbreaker.com")
                    .filters { f ->
                        f.circuitBreaker { config ->
                            config.setName("mycmd")
                            config.setFallbackUri("forward:/fallback")
                        }
                    }
                    .uri("http://httpbin.org:80")
            }
            .build()
    }
}
