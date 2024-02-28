package org.octopusden.octopus.jira.vcs.ui.vcspanel

import com.atlassian.plugin.web.Condition
import org.octopusden.octopus.jira.vcs.config.PluginProperty
import org.octopusden.octopus.jira.vcs.config.PluginSettings

class VcsPanelConfiguration(private val pluginSettings: PluginSettings) : Condition {
    override fun shouldDisplay(context: MutableMap<String, Any>) =
        context["issue"]?.let { issue ->
            pluginSettings.getBoolean(PluginProperty.VCS_PANEL_DISPLAY)
        } ?: false

    override fun init(params: MutableMap<String, String>?) {

    }
}
