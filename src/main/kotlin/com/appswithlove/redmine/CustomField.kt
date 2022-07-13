package com.appswithlove.redmine

@kotlinx.serialization.Serializable
data class CustomField(
    val id: Int,
    val name: String,
    val value: String? = null
)