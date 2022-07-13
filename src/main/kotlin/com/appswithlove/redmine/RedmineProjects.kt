import com.appswithlove.redmine.CustomField

@kotlinx.serialization.Serializable
data class RedmineProjects(
    val limit: Int,
    val offset: Int,
    val projects: List<ProjectX>,
    val total_count: Int
)

@kotlinx.serialization.Serializable
data class ProjectX(
    val created_on: String,
    val custom_fields: List<CustomField>,
    val description: String = "",
    val id: Int,
    val identifier: String,
    val name: String,
    val parent: Parent? = null,
    val status: Int,
    val updated_on: String
)

@kotlinx.serialization.Serializable
data class Parent(
    val id: Int,
    val name: String
)