package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import java.util.Date
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

    override fun getCommits(issueKey: String): VcsFacadeService.Repositories<VcsFacadeService.Commit> =
        with(vcsFacadeClient.findCommitsByIssueKey(issueKey)) {
            val repositoryCommits = groupBy { c -> c.vcsUrl }
                .map { (vcsUrl, commits) ->
                    VcsFacadeService.RepositoryEntities(
                        vcsUrl,
                        //ToDo 'repository.avatar'
                        null,
                        commits.map { c ->
                            VcsFacadeService.Commit(
                                c.id, c.link, c.message, c.date, VcsFacadeService.Author(c.authorAvatarUrl, c.author)
                            )
                        })
                }
            VcsFacadeService.Repositories(size, repositoryCommits)
        }

    override fun getPullRequests(issueKey: String): Collection<VcsFacadeService.PullRequest> =
        with(vcsFacadeClient.findPullRequestsByIssueKey(issueKey)) {
            this.map { pr ->
                VcsFacadeService.PullRequest(
                    pr.id,
                    pr.link,
                    pr.title,
                    //ToDo 'creator' (author)
                    VcsFacadeService.Author(
                        pr.assignee.firstOrNull()?.avatarUrl, pr.assignee.firstOrNull()?.name ?: "Joe Doe"
                    ),
                    //ToDo 'approved'
                    pr.reviewers.map { r -> VcsFacadeService.Reviewer(r.name, r.avatarUrl, false) },
                    VcsFacadeService.IssuePullRequestSummary.PullRequestStatus.valueOf(pr.status.name),
                    pr.updatedAt,
                    pr.target
                )
            }
        }

    override fun getBranches(issueKey: String): VcsFacadeService.Repositories<VcsFacadeService.Branch> =
        with(vcsFacadeClient.findBranchesByIssueKey(issueKey)) {
            val repositoryBranches = this.groupBy { b -> b.vcsUrl }.map { (vcsUrl, branches) ->
                VcsFacadeService.RepositoryEntities(
                    vcsUrl,
                    //ToDo 'repository.avatar'
                    null,
                    branches.map { b -> VcsFacadeService.Branch(b.name, b.link, Date()) })
            }
            VcsFacadeService.Repositories(this.size, repositoryBranches)
        }

    private fun getClient(): VcsFacadeClient =
        ClassicVcsFacadeClient(vcsFacadeClientParametersProvider).also { log.info("Init VCS Facade API client") }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
