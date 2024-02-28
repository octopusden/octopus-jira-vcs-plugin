package org.octopusden.octopus.jira.vcs.config

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import org.octopusden.octopus.jira.exception.JiraApplicationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties


private const val PROPERTIES_FILE_NAME = "/plugin.properties"

class PluginSettings(pluginSettingsFactory: PluginSettingsFactory) {

    private val properties = Properties().apply {
        PluginSettings::class.java.getResourceAsStream(PROPERTIES_FILE_NAME)
            .use { stream ->
                load(stream)
            }
    }

    private val pluginSettings = pluginSettingsFactory.createGlobalSettings()

    operator fun get(setting: PluginProperty): Any =
        pluginSettings[setting.key] ?: getProperty(setting.key)

    operator fun set(key: PluginProperty, value: String?) {
        pluginSettings.put(key.key, value)
    }

    fun getString(key: PluginProperty) =
        get(key) as? String ?: throw JiraApplicationException("Format '${key.key}' must be String")

    fun getList(key: PluginProperty) = getString(key)
        .split(",")
        .map(String::trim)

    fun getLong(key: PluginProperty) = getString(key)
        .toLong()

    fun getDate(key: PluginProperty, format: SimpleDateFormat): Date = format.parse(getString(key))

    fun getBoolean(property: PluginProperty): Boolean = getString(property).toBoolean()

    private fun getProperty(propertyName: String): String {
        return properties.getProperty(propertyName)
            ?: throw JiraApplicationException("Property [$propertyName] must be defined in the property file [$PROPERTIES_FILE_NAME]")
    }

    companion object {
        val DATE_FORMAT_WITHOUT_TIME = SimpleDateFormat("yyyy/MM/dd")
    }
}
