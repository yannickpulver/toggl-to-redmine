import java.time.LocalDate

@kotlinx.serialization.Serializable
data class RedmineTimeEntry(
    val issue_id: Int? = null,
    val project_id: Int? = null,
    val spent_on: String,
    val hours: Float,
    val activity_id: Int? = null,
    val comments: String
)

@kotlinx.serialization.Serializable
data class RedmineTimeEntryContainer(
    val time_entry: RedmineTimeEntry
)

@kotlinx.serialization.Serializable
data class RedmineTimeEntries(
    val limit: Int,
    val offset: Int,
    val time_entries: List<RedmineTimeEntry>,
    val total_count: Int
)



