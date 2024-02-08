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

    private fun getClient(): VcsFacadeClient = ClassicVcsFacadeClient(vcsFacadeClientParametersProvider)
        .also { log.info("Init VCS Facade API client") }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
