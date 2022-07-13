package com.appswithlove.store

import java.util.prefs.Preferences

class DataStore {

    private val field_togglApiKey = "togglApiKey"
    private val field_redmineApiKey = "redmineApiKey"
    private val field_redmineUrl = "redmineUrl"
    private val field_redmineDefaultActivity = "redmineDefaultActivity"
    private val field_amountTimeEntries = "amountTimeEntries"
    private val preferences = Preferences.userNodeForPackage(javaClass)

    val getStore: Store
        get() {
            val togglApiKey = preferences[field_togglApiKey, ""]
            val redmineApiKey = preferences[field_redmineApiKey, ""]
            val redmineUrl = preferences[field_redmineUrl, ""]
            val defaultActivity = preferences.getInt(field_redmineDefaultActivity, -1)
            return Store(togglApiKey, redmineApiKey, redmineUrl, defaultActivity)
        }

    fun setTogglApiKey(apiKey: String?) {
        preferences.put(field_togglApiKey, apiKey.orEmpty())
    }

    fun setRedmineApiKey(apiKey: String?) {
        preferences.put(field_redmineApiKey, apiKey.orEmpty())
    }

    fun setRedmineUrl(url: String?) {
        preferences.put(field_redmineUrl, url.orEmpty())
    }

    fun setRedmineDefaultActivity(activity: Int?) {
        preferences.putInt(field_redmineDefaultActivity, activity ?: -1)
    }

    fun addAndGetTimeEntryCount(entriesUploaded: Int): Int {
        val count = preferences.getInt(field_amountTimeEntries, 0)
        val newCount = count + entriesUploaded
        preferences.putInt(field_amountTimeEntries, newCount)
        return newCount
    }
}