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
data class GetCategoryRequest(
    val category_id: String
)

@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val description: String,
    val color_code: String
)
@Serializable
data class AddBudgetRequest(
    val category_id: String,
    val amount: Float,
    val start_date: String,
    val end_date: String
)

@Serializable
data class UpdateBudgetRequest(
    val category_id: String,
    val amount: Float,
    val start_date: String,
    val end_date: String
)

@Serializable
data class UpdateExpenseRequest(
    val amount: Float,
    val category_id: String,
    val date: String,
    val description: String
)

@Serializable
data class AddExpenseRequest(
    val category_id: String,
    val amount: Float,
    val date: String,
    val description: String
)