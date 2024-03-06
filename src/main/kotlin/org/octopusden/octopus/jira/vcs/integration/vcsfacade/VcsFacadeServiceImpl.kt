package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.ClassicVcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VcsFacadeServiceImpl(
    private val vcsFacadeClientParametersProvider: VcsFacadeClientParametersProvider
) : VcsFacadeService {

    private var vcsFacadeClient: VcsFacadeClient = getClient()

    override fun updateConnection() {
        vcsFacadeClient = getClient()
    }

    override fun getSummary(issueKey: String): VcsFacadeService.IssueVcsSummary {
        TODO("Not yet implemented")
    }

    override fun getCommits(issueKey: String): VcsFacadeService.Commits {
        TODO("Not yet implemented")
    }

    override fun getPullRequests(issueKey: String): Collection<VcsFacadeService.PullRequest> {
        TODO("Not yet implemented")
    }

    override fun getBranches(issueKey: String): VcsFacadeService.Branches {
        TODO("Not yet implemented")
    }

    private fun getClient(): VcsFacadeClient = ClassicVcsFacadeClient(vcsFacadeClientParametersProvider)
        .also { log.info("Init VCS Facade API client") }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
