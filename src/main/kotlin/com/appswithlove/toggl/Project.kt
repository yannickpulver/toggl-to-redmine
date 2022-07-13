package com.appswithlove.toggl

@kotlinx.serialization.Serializable
data class Project(
    val active: Boolean,
    val actual_hours: Int,
    val at: String,
    val cid: Int?,
    val client_id: Int?,
    val color: String,
    val created_at: String,
    val id: Int,
    val is_private: Boolean,
    val name: String,
    val recurring: Boolean,
    val wid: Int,
    val workspace_id: Int
) {
    val projectId get() = "\\((\\d*)\\)".toRegex().find(name)?.groupValues?.lastOrNull()?.toIntOrNull()
}
