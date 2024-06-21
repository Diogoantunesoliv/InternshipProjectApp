package com.example.internshipprojectapp.data.model

data class ApiResponse(
    val name: String,
    val value: String,
    val resourceNotFound: Boolean,
    val searchedLocation: String
)
