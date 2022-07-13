package com.appswithlove

import TimeEntryForPublishing
import com.appswithlove.redmine.RedmineRepo
import com.appswithlove.store.DataStore
import com.appswithlove.toggl.TogglProject
import com.appswithlove.toggl.TogglRepo
import java.time.LocalDate

class T2R {
    val dataStore = DataStore()
    val redmine = RedmineRepo(dataStore)
    val toggl = TogglRepo(dataStore)

    fun fetchProjects() {
        val workspace = toggl.getWorkspaces() ?: throw Exception("Couldn't get Toggle Workspace")

        val redmineProjects = redmine.getRedmineProjects()
        val togglProjects = toggl.getTogglProjects()

        val newProjects =
            redmineProjects.filter { redmineProject -> !togglProjects.any { it.name.contains("(${redmineProject.key})") } }
                .map { TogglProject(name = "${it.value} (${it.key})") }


        if (newProjects.isNotEmpty()) {
            println("⬆️ Syncing new Redmine projects to Toggl — (${newProjects.size}) of ${redmineProjects.size}")
            println("---")
        } else {
            println("🎉 All Redmine Projects already up-to-date in Toggl!")
            return
        }

        toggl.pushProjectsToToggl(workspace.id, newProjects)
    }


    fun addTimeEntries(date: LocalDate) {
        val timeEntries = toggl.getTogglTimeEntries(date)
        println("⏱ Found ${timeEntries.size} time entries for $date on Toggl!")
        if (timeEntries.isEmpty()) {
            println("Noting to do here. Do you even work?")
            return
        }
        val projects = toggl.getTogglProjects()
        val pairs = timeEntries.map { time -> time to projects.firstOrNull { it.id == time.project_id } }
        val activities = redmine.getRedmineActivities().time_entry_activities

        val timeEntriesOnDate = redmine.getRedmineTimeEntries(date).time_entries
        if (timeEntriesOnDate.isNotEmpty()) {
            println("---")
            System.err.println("⚠️ There are already existing time entries for that date. Can't guarantee to not mess up. So please remove them first here: ${redmine.getRedmineUrl()}time_entries?user_id=me&spent_on=$date.")
            return
        }

        val defaultActivity = redmine.getDefaultRedmineActivity(activities)

        if (pairs.any { it.second?.projectId == null }) {
            System.err.println("⚠️ Some time entries don't have a valid project assigned. Please fix this and try again.")
            return
        }

        val data = pairs.map {
            TimeEntryForPublishing(
                timeEntry = it.first,
                projectId = it.second?.projectId ?: -1,
                activityId = activities.firstOrNull { activity -> activity.name == it.first.tags?.firstOrNull() }?.id
                    ?: defaultActivity
            )
        }

        redmine.pushToRedmine(date, data)
    }


}