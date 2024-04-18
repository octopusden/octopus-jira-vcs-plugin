package org.octopusden.octopus.jira.vcs.ui.vcspanel

import com.atlassian.cache.Cache
import com.atlassian.cache.CacheManager
import com.atlassian.cache.CacheSettingsBuilder
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider
import com.atlassian.jira.plugin.webfragment.model.JiraHelper
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.plugin.webresource.WebResourceUrlProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.apache.velocity.tools.generic.DateTool
import org.octopusden.octopus.jira.vcs.config.PluginProperty
import org.octopusden.octopus.jira.vcs.config.PluginSettings
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService

class VcsPanelContextProvider @Inject constructor(
    private val vcsFacadeService: VcsFacadeService,
    @ComponentImport private val webResourceUrlProvider: WebResourceUrlProvider,
    @ComponentImport private val cacheManager: CacheManager,
    private val pluginSettings: PluginSettings
) : AbstractJiraContextProvider() {

    private val cache: Cache<String, VcsFacadeService.IssueVcsSummary>? =
        with(pluginSettings.getLong(PluginProperty.VCS_PANEL_CACHE_SUMMARY_EXPIRE_AFTER_SECS)) {
            if (this > 0) {
                cacheManager.getCache(
                    "${VcsPanelContextProvider::class.qualifiedName}.cache",
                    null,
                    CacheSettingsBuilder().expireAfterWrite(
                        this,
                        TimeUnit.SECONDS
                    ).build()
                )
            } else null
        }

    override fun getContextMap(user: ApplicationUser, jiraHelper: JiraHelper): MutableMap<String, Any> {
        val contextMap = mutableMapOf<String, Any>()
        (jiraHelper.contextParams["issue"] as? Issue)
            ?.key
            ?.let { issueKey ->
                val vcsInformation = cache?.get(issueKey) { issueKey.toVcsSummary() } ?: issueKey.toVcsSummary()
                contextMap["branches"] = vcsInformation.branches
                contextMap["commits"] = vcsInformation.commits
                contextMap["pullRequests"] = vcsInformation.pullRequests
                contextMap["dateTool"] = DateTool()
                contextMap["issueKey"] = issueKey
                contextMap["jiraBaseUrl"] = webResourceUrlProvider.baseUrl
            }
        return contextMap
    }

    private fun String.toVcsSummary() = vcsFacadeService.getSummary(this)
}
