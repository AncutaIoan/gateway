package adamicus.gateway.model


data class UserPayload (
    val userId: Long
)

data class LoginRequest(
    val email: String,
    val password: String
)