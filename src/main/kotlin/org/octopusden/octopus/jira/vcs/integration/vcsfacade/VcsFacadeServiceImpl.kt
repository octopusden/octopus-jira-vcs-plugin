package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.ClassicVcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Date

class VcsFacadeServiceImpl(
    private val vcsFacadeClientParametersProvider: VcsFacadeClientParametersProvider
) : VcsFacadeService {

    private var vcsFacadeClient: VcsFacadeClient = getClient()

    override fun updateConnection() {
        vcsFacadeClient = getClient()
    }

    // ToDo MOCK:remove after integration is finished
    private val started = Date()

    override fun getVcsInformation(issueKey: String): VcsFacadeService.VcsInformation {
        // ToDo MOCK:remove after integration is finished
        return VcsFacadeService.VcsInformation(
            VcsFacadeService.BranchInfo(3, started),
            VcsFacadeService.CommitInfo(2, started),
            VcsFacadeService.PullRequestInfo(1, VcsFacadeService.PullRequestInfo.Status.DECLINED, started)
        )
    }

    private fun getClient(): VcsFacadeClient = ClassicVcsFacadeClient(vcsFacadeClientParametersProvider)
        .also { log.info("Init VCS Facade API client") }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
