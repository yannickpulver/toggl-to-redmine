package com.appswithlove.redmine

@kotlinx.serialization.Serializable
data class RedmineUser(
    val user: User
)

@kotlinx.serialization.Serializable
data class User(
    val api_key: String,
    val created_on: String,
    val firstname: String,
    val id: Int,
    val last_login_on: String,
    val lastname: String,
    val login: String,
    val mail: String,
    val status: Int
)