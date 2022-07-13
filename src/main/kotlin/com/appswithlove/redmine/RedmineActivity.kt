import com.appswithlove.redmine.CustomField

@kotlinx.serialization.Serializable
data class RedmineActivity(
    val time_entry_activities: List<TimeEntryActivity>
)

@kotlinx.serialization.Serializable
data class TimeEntryActivity(
    val custom_fields: List<CustomField>,
    val id: Int,
    val name: String
)