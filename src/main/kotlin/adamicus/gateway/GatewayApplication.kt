package adamicus.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod

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
            .route("ping-route") { p ->
                p.path("/ping")
                    .and().method(HttpMethod.GET)
                    .uri("http://localhost:8081")
            }
            .route("user-service") { p ->
                p.path("/api/auth/**")
                    .uri("http://localhost:8081")
            }
            .build()
    }
}