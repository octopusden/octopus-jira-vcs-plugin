package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import org.octopusden.octopus.jira.vcs.config.PluginProperty
import org.octopusden.octopus.jira.vcs.config.PluginSettings
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider

class JiraVcsPluginVcsFacadeClientParametersProvider(private val pluginSettings: PluginSettings) :
    VcsFacadeClientParametersProvider {
    override fun getApiUrl(): String = pluginSettings.getString(PluginProperty.VCS_FACADE_API_URL)

    override fun getTimeRetryInMillis(): Int =
        pluginSettings.getString(PluginProperty.VCS_FACADE_RETRY_DELAY_MILLIS).toInt()
}
