package org.octopusden.octopus.jira.vcs.config

enum class PluginProperty(val key: String, val displayName: String, val sensitive: Boolean) {
    VCS_FACADE_API_URL("vcs-facade.api.url", "VCS Facade API URL", false),
    VCS_FACADE_RETRY_DELAY_MILLIS("vcs-facade.retry.delay.millis", "VCS Facade API retry timeout (msecs)", false),

    VCS_PANEL_DISPLAY("octopus-vcs-plugin.vcs-panel.show", "Display VCS Panel", false),
    VCS_PANEL_CACHE_SUMMARY_EXPIRE_AFTER_SECS("octopus-vcs-plugin.vcs-panel.cache.summary.expire-after.secs", "VCS Panel Summary Cache Expire After (secs)", false),

    VCS_PANEL_COMMIT_FILE_LIMIT("octopus-vcs-plugin.vcs-panel.commit.file-limit", "File limit per commit", false);

    companion object {
        fun valueOfPropertyName(propertyName: String): PluginProperty? {
            return entries.firstOrNull { it.key == propertyName }
        }
    }
}
