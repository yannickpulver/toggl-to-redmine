package com.appswithlove.redmine

import ProjectX
import RedmineActivity
import RedmineProjects
import RedmineTimeEntries
import RedmineTimeEntry
import RedmineTimeEntryContainer
import TimeEntryActivity
import TimeEntryForPublishing
import com.appswithlove.DataStore
import com.appswithlove.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

class RedmineRepo constructor(private val dataStore: DataStore) {

    fun pushToRedmine(date: LocalDate, pairs: List<TimeEntryForPublishing>) {
        println("⬆️ Uploading ${pairs.size} time entries to Redmine!")
        println("---")
        val redmineUrl = getRedmineUrl()
        val redmineApiKey = getRedmineApiKey()
        val endpoint = "$redmineUrl/time_entries.json?key=$redmineApiKey"

        val timeEntries = pairs.map {

            val issueId = Regex("^\\d{5}").findAll(it.timeEntry.description).firstOrNull()?.value?.toIntOrNull()
            val description = when (issueId) {
                null -> it.timeEntry.description
                else -> Regex("\\d{5} ?-? ?").replace(it.timeEntry.description, "")
            }

            RedmineTimeEntry(
                project_id = if (issueId == null) it.projectId else null,
                issue_id = issueId,
                spent_on = it.timeEntry.start.split("T").firstOrNull().orEmpty(),
                hours = it.timeEntry.duration / 60f / 60f,
                comments = description,
                activity_id = it.activityId
            )
        }.map { RedmineTimeEntryContainer(it) }

        timeEntries.forEachIndexed { index, it ->
            val data = json.encodeToString(it)
            val request = postRequest(endpoint, data)
            if (request.statusCode() != 201) {
                System.err.println("An error occurred when uploading: ${it.time_entry.comments}")
                System.err.println(request.body() + request.statusCode())
                return
            }
            println("Posting (${index + 1}/${timeEntries.size}): ${it.time_entry.comments}")
        }
        println("💯 Uploaded all time entries to Redmine for $date")
        val totalEntriesSaved = dataStore.addAndGetTimeEntryCount(timeEntries.size)
        val timeSaved = getTimeSaved(timeEntries.size)
        val totalTimeSaved = getTimeSaved(totalEntriesSaved)
        println("---")
        println("🎉 You just saved $timeSaved. And a total of $totalTimeSaved!")
    }

    private fun getTimeSaved(size: Int): String {
        val seconds = size * 30
        return when {
            seconds > 3600 -> "${"%.2f".format(seconds / 60f / 60f)}h"
            seconds > 60 -> "${(seconds / 60f).roundToInt()}m"
            else -> "${seconds}s"
        }
    }


    fun getRedmineUrl(): String {
        var url: String? = dataStore.getStore.redmineUrl
        while (url.isNullOrEmpty()) {
            println("🔗 Setup Redmine Url: Add your Url here (for example: https://redmine.abc.com/) + click Enter")
            url = readLine().orEmpty()
            dataStore.setRedmineUrl(url)
        }
        return url
    }

    private fun getRedmineApiKey(): String {
        val redmineUrl = getRedmineUrl()
        var key: String? = dataStore.getStore.redmineKey
        while (key.isNullOrEmpty()) {
            println("🔑 Setup Redmine API Key: Please visit ${redmineUrl}/my/account and copy the key from the 'API access key' section here + click Enter:")
            key = readLine()
            dataStore.setRedmineApiKey(key)
        }
        return key
    }

    fun getDefaultRedmineActivity(activities: List<TimeEntryActivity>): Int {
        var defaultActivity: Int? = dataStore.getStore.defaultActivity
        while (defaultActivity == null || defaultActivity == -1) {
            println("📜 Setup Redmine Default Activity:")
            activities.forEach {
                println("${it.id}: ${it.name}")
            }
            println("----")
            println("ℹ️ Add Number of desired activity + press Enter:")
            defaultActivity = readLine()?.toIntOrNull()

            if (!activities.any { it.id == defaultActivity }) {
                defaultActivity = -1
            } else {
                dataStore.setRedmineDefaultActivity(defaultActivity)
            }
        }
        return defaultActivity
    }

    fun getRedmineTimeEntries(date: LocalDate): RedmineTimeEntries {
        val redmineUrl = getRedmineUrl()
        val redmineApiKey = getRedmineApiKey()
        val userId = getRedmineUserId()
        val endpoint = "$redmineUrl/time_entries.json?from=$date&to=$date&key=$redmineApiKey&user_id=$userId"

        val response = getRequest(url = endpoint)
        return json.decodeFromString(response.body())
    }

    private fun getRedmineUserId(): Int {
        val redmineUrl = getRedmineUrl()
        val redmineApiKey = getRedmineApiKey()
        val endpoint = "${redmineUrl}/users/current.json?key=$redmineApiKey"
        val response = getRequest(url = endpoint)
        return json.decodeFromString<RedmineUser>(response.body()).user.id
    }

    fun getRedmineActivities(): RedmineActivity {
        val redmineUrl = getRedmineUrl()
        val redmineApiKey = getRedmineApiKey()
        val api = "$redmineUrl/enumerations/time_entry_activities.json?key=$redmineApiKey"
        val response = getRequest(url = api)
        return json.decodeFromString(response.body())
    }

    fun getRedmineProjects(): Map<Int, String> {
        val projectList = mutableListOf<ProjectX>()
        var offset = 0

        val redmineUrl = getRedmineUrl()
        val redmineApiKey = getRedmineApiKey()


        println("Downloading Redmine Projects ⬇️")
        while (true) { //fcking dangerous
            val api = "$redmineUrl/projects.json?key=$redmineApiKey&limit=100&offset=$offset"
            val response = getRequest(url = api)
            val projects = json.decodeFromString<RedmineProjects>(response.body())
            if (projects.projects.isEmpty()) break
            projectList.addAll(projects.projects)
            println("Downloading - Progress: ${(projectList.size.toFloat() / projects.total_count) * 100f}%")
            offset += 100
        }

        return projectList.associate { it.id to it.name }
    }

    private fun getRequest(
        url: String
    ): HttpResponse<String> {
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder().uri(URI.create(url)).GET()
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    }

    private fun postRequest(url: String, data: String): HttpResponse<String> {
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder().uri(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(data))
            .header("Content-Type", "application/json")
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    }
}