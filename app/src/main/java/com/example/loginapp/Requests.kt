import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String

)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class EmailRequest(
    val email: String
)