package org.octopusden.octopus.jira.vcs.ui.vcspanel

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider
import com.atlassian.jira.plugin.webfragment.model.JiraHelper
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.plugin.webresource.WebResourceUrlProvider
import javax.inject.Inject
import org.apache.velocity.tools.generic.DateTool
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService

class VcsPanelContextProvider @Inject constructor(
    private val vcsFacadeService: VcsFacadeService,
    @ComponentImport private val webResourceUrlProvider: WebResourceUrlProvider
) : AbstractJiraContextProvider() {

    override fun getContextMap(user: ApplicationUser, jiraHelper: JiraHelper): MutableMap<String, Any> {
        val contextMap = mutableMapOf<String, Any>()
        (jiraHelper.contextParams["issue"] as? Issue)
            ?.key
            ?.let { issueKey ->
                val vcsInformation = vcsFacadeService.getSummary(issueKey)
                contextMap["branches"] = vcsInformation.branches
                contextMap["commits"] = vcsInformation.commits
                contextMap["pullRequests"] = vcsInformation.pullRequests
                contextMap["dateTool"] = DateTool()
                contextMap["issueKey"] = issueKey
                contextMap["jiraBaseUrl"] = webResourceUrlProvider.baseUrl
            }
        return contextMap
    }
}
