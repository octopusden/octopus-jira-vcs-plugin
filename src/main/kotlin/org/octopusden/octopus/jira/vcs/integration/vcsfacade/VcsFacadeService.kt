package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import java.util.Date

interface VcsFacadeService {
    fun updateConnection()
    fun getVcsInformation(issueKey: String): VcsInformation
    data class VcsInformation(val branches: BranchInfo, val commits: CommitInfo, val pullRequests: PullRequestInfo)
    data class BranchInfo(val size: Int, val updated: Date)
    data class PullRequestInfo(val size: Int, val status: Status, val updated: Date) {
        enum class Status(val style: String) {
            OPEN("info"),
            MERGED("success"),
            DECLINED("error")
        }
    }

    data class CommitInfo(val size: Int, val latest: Date)
}
