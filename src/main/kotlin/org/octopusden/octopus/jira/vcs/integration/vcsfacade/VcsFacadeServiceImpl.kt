package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import java.util.Date
import javax.inject.Named
import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.ClassicVcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Named
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
        with(vcsFacadeClient.findCommitsByIssueKey(issueKey.also { log.info("Get Commits for '{}'", it) })) {
            val repositoryCommits = groupBy { c -> c.repository }
                .map { (repository, commits) ->
                    VcsFacadeService.RepositoryEntities(
                        repository.link,
                        repository.avatar,
                        commits.map { c ->
                            VcsFacadeService.Commit(
                                c.id, c.link, c.message, c.date, VcsFacadeService.Author(c.author.avatar, c.author.name)
                            )
                        })
                }
            VcsFacadeService.Repositories(size, repositoryCommits)
        }

    override fun getPullRequests(issueKey: String): Collection<VcsFacadeService.PullRequest> =
        with(vcsFacadeClient.findPullRequestsByIssueKey(issueKey.also { log.info("Get Pull Requests for '{}'", it) })) {
            this.map { pr ->
                VcsFacadeService.PullRequest(
                    pr.link,
                    pr.title,
                    VcsFacadeService.Author(pr.author.avatar, pr.author.name),
                    pr.reviewers.map { r -> VcsFacadeService.Reviewer(r.user.name, r.user.avatar, r.approved) },
                    VcsFacadeService.IssuePullRequestSummary.PullRequestStatus.valueOf(pr.status.name),
                    pr.updatedAt,
                    pr.target
                )
            }
        }

    override fun getBranches(issueKey: String): VcsFacadeService.Repositories<VcsFacadeService.Branch> =
        with(vcsFacadeClient.findBranchesByIssueKey(issueKey.also { log.info("Get Branches for '{}'", it) })) {
            val repositoryBranches = this.groupBy { b -> b.repository }.map { (repository, branches) ->
                VcsFacadeService.RepositoryEntities(
                    repository.link,
                    repository.avatar,
                    branches.map { b -> VcsFacadeService.Branch(b.name, b.link, Date()) })
            }
            VcsFacadeService.Repositories(this.size, repositoryBranches)
        }

    private fun getClient(): VcsFacadeClient =
        ClassicVcsFacadeClient(vcsFacadeClientParametersProvider).also { log.info("Init VCS Facade API client, URL: {}", vcsFacadeClientParametersProvider.getApiUrl()) }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
