package org.octopusden.octopus.jira.vcs.config

enum class PluginProperty(val key: String, val displayName: String, val sensitive: Boolean) {
    VCS_FACADE_API_URL("vcs-facade.api.url", "VCS Facade API URL", false),
    VCS_FACADE_RETRY_DELAY_MILLIS("vcs-facade.retry.delay.millis", "VCS Facade API retry timeout", false),

    VCS_PANEL_DISPLAY("octopus-vcs-plugin.vcs-panel.show", "Display VCS Panel", false);

    companion object {
        fun valueOfPropertyName(propertyName: String): PluginProperty? {
            return entries.firstOrNull { it.key == propertyName }
        }
    }
}
