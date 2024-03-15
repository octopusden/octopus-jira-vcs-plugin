package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.ClassicVcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class VcsFacadeServiceImpl(
    private val vcsFacadeClientParametersProvider: VcsFacadeClientParametersProvider
) : VcsFacadeService {

    private var vcsFacadeClient: VcsFacadeClient = getClient()

    override fun updateConnection() {
        vcsFacadeClient = getClient()
    }

    override fun getSummary(issueKey: String): VcsFacadeService.IssueVcsSummary =
        with(vcsFacadeClient.findByIssueKey(issueKey)) {
            VcsFacadeService.IssueVcsSummary(
                VcsFacadeService.IssueBranchSummary(
                    branches.size, branches.updated
                ), VcsFacadeService.IssueCommitSummary(
                    commits.size, commits.latest
                ), VcsFacadeService.IssuePullRequestSummary(
                    pullRequests.size, pullRequests.status?.let { pullRequestStatus ->
                        VcsFacadeService.IssuePullRequestSummary.PullRequestStatus.valueOf(
                            pullRequestStatus.name
                        )
                    }, pullRequests.updated
                )
            )
        }

    override fun getCommits(issueKey: String): VcsFacadeService.Commits =
        with(vcsFacadeClient.findCommitsByIssueKey(issueKey)) {
            val repositoryCommits = this.groupBy { c -> c.vcsUrl }.map { (vcsUrl, commits) ->
                    VcsFacadeService.RepositoryCommits(vcsUrl.vcsUrlToRepoName(), vcsUrl, null, commits.map { c ->
                        VcsFacadeService.Commit(
                            c.id, c.link, c.message, c.date, VcsFacadeService.Author(c.authorAvatarUrl, c.author)
                        )
                    })
                }
            VcsFacadeService.Commits(size, repositoryCommits)
        }

    override fun getPullRequests(issueKey: String): Collection<VcsFacadeService.PullRequest> =
        with(vcsFacadeClient.findPullRequestsByIssueKey(issueKey)) {
            this.map { pr ->
                VcsFacadeService.PullRequest(
                    pr.id,
                    pr.link,
                    pr.title,
                    VcsFacadeService.Author(
                        pr.assignee.firstOrNull()?.avatarUrl, pr.assignee.firstOrNull()?.name ?: "Joe Doe"
                    ),
                    pr.reviewers.map { r -> VcsFacadeService.Reviewer(r.name, r.avatarUrl, false) },
                    VcsFacadeService.IssuePullRequestSummary.PullRequestStatus.valueOf(pr.status.name),
                    pr.updatedAt,
                    pr.target
                )
            }
        }

    override fun getBranches(issueKey: String): VcsFacadeService.Branches =
        with(vcsFacadeClient.findBranchesByIssueKey(issueKey)) {
            val repositoryBranches = this.groupBy { b -> b.vcsUrl }.map { (vcsUrl, branches) ->
                VcsFacadeService.RepositoryBranches(vcsUrl,
                    vcsUrl,
                    "",
                    branches.map { b -> VcsFacadeService.Branch(b.name, b.link, Date()) })
            }
            VcsFacadeService.Branches(this.size, repositoryBranches)
        }

    private fun String.vcsUrlToRepoName(): String = substring(lastIndexOf("/") + 1, indexOf(".git"))

    private fun getClient(): VcsFacadeClient =
        ClassicVcsFacadeClient(vcsFacadeClientParametersProvider).also { log.info("Init VCS Facade API client") }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
