package com.example.loginapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val status: Int,
    val message: String,
    val data: userData
)

@Serializable
data class userData(
    val userId: String
)

@Serializable
data class RegisterErrResponse(
    val status: Int,
    val message: String,
    val errors: errorData
)

@Serializable
data class errorData(
    val error: String
)

@Serializable
data class LoginResponse(
    val status: Int,
    val message: String,
    val data: userResponseData
)

@Serializable
data class userResponseData(
    val token: String,
    val userId: String
)

@Serializable
data class LoginErrResponse(
    val status: Int,
    val message: String,
)

@Serializable
data class resetPasswordReqResponse(
    val status: Int,
    val message: String,
    val data: emailReqResponseData
)

@Serializable
data class emailReqResponseData(
    val message: String,
    val status: String
)

@Serializable
data class resetPasswordReqErrResponse(
    val status: Int,
    val message: String
)

@Serializable
data class resetPasswordResponse(
    val status: Int,
    val message: String,
)

@Serializable
data class resetPasswordErrResponse(
    val status: Int,
    val message: String
)

@Serializable
data class UserDataResponse(
    val status: Int,
    val message: String,
    val data: InnerUserDataResponse,
    val errors: String? = null  // Nullable to handle the case where there are no errors
)

@Serializable
data class InnerUserDataResponse(
    val first_name: String,
    val last_name: String,
    val email: String,
    val created_at: String

)

@Serializable
data class GetUserPasswordResponse(
    val status: Int,
    val message: String

)

@Serializable
data class UpdateUserResponse(
    val status: Int,
    val message: String
)

data class Transaction(
    val label: String,
    val amount: Double,
)
data class CategoryTest(
    val category: String,
    val amount: Double,
)
@Serializable
data class CategoryDefaultDataResponse(
    val status: Int,
    val message: String,
    val data: CategoryData
)

@Serializable
data class GetAllCategoriesResponse(
    val status: Int,
    val message: String,
    val data: List<Category>
)
@Serializable
data class CategoryData(
    val categories: List<Category> // List of category objects
)
@Serializable
data class Category(
    @SerialName("category_id") val categoryId: String, // UUID for category
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    val description: String,
    @SerialName("color_code") val colorCode: String,
    @SerialName("is_default") val isDefault: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("deleted_at") val deletedAt: String? // It can be null
)

@Serializable
data class AddCategoryResponse(
    val status: Int,
    val message: String,
    val data: String
)
@Serializable
data class AddCategoryErrResponse(
    val error: String
)
@Serializable
data class DeleteCategoryResponse(
    val status: Int,
    val message: String
)

@Serializable
data class UpdateCategoryResponse(
    val status: Int,
    val message: String,
    val data: Category
)

@Serializable
data class AddBudgetResponse(
    val status: Int,
    val message: String,
    val data: Budget
)

@Serializable
data class Budget(
    @SerialName("budget_id") val budget_id: String,
    @SerialName("user_id") val user_id: String,
    @SerialName("category_id") val category_id: String,
    val amount: Float,
    @SerialName("start_date") val start_date: String,
    @SerialName("end_date") val end_date: String,
    @SerialName("created_at") val created_at: String,
    @SerialName("updated_at") val updated_at: String,
    @SerialName("deleted_at") val deleted_at: String?
)
@Serializable
data class GetAllBudgetsResponse(
    val status: Int,
    val message: String,
    val data: List<Budget>
)
@Serializable
data class DeleteBudgetResponse(
    val status: Int,
    val message: String

)


