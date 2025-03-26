package com.example.agritechdisease

data class ApiResponse(
    val disease_name: String,
    val confidence: Float,
    val description: String
)
