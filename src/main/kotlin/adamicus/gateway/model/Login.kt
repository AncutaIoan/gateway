package adamicus.gateway.model

data class LoginResponse (
    val userId: Long,
    val userName: String,
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)