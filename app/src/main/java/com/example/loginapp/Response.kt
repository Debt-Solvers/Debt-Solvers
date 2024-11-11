package com.example.loginapp

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
    val oldPassword: String,
    val newPassword: String

)

