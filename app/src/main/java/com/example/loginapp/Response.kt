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

