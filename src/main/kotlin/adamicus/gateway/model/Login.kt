package adamicus.gateway.model

import adamicus.gateway.common.annotations.NoArgsConstructor
import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginResponse (
    val userId: Long,
    val userName: String,
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)