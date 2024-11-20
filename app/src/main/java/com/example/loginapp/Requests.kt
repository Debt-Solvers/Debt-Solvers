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

@Serializable
data class ConfirmResetRequest(
    val token: String,
    val new_password: String
)
@Serializable
data class ChangeUserPasswordRequest(
    val current_password: String,
    val new_password: String

)
@Serializable
data class UpdateUserRequest(
    val first_name: String,
    val last_name: String,
    val email: String
)

@Serializable
data class AddCategoryRequest(
    val name: String,
    val description: String
)
@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val description: String,
    val color_code: String
)
