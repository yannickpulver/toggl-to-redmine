package com.appswithlove

import kotlinx.serialization.json.Json
import java.time.LocalDate

val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

fun main(args: Array<String>) {

    val t2r = T2R()
    when (args.firstOrNull()) {
        "fetchProjects" -> t2r.fetchProjects()
        "addTime" -> {
            val date: String? = args.getOrNull(1)
            if (date == null) {
                System.err.println("Please pass in a valid date in format: addTime 2022-07-11")
                return
            }
            t2r.addTimeEntries(LocalDate.parse(date))
        }
        else -> {
            println("Welcome to Toggl to Redmine. Please run the app with following arguments")
            println("-------")
            println("'fetchProjects'")
            println("     - to fetch all projects from Redmine and add them to Toggl")
            println("'addTime DATE'")
            println("     - to add time entries from toggl to redmine for a given date.")
            println("     - Date Format: 2022-06-24")
        }
    }
}

