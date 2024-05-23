package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import java.util.Date
import javax.inject.Named
import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.common.exception.VcsFacadeException
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

    override fun getSummary(issueKey: String): VcsFacadeService.IssueVcsSummary = try {
        with(vcsFacadeClient.findByIssueKey(issueKey.also { log.info("Get VCS Summary for '{}'", it) })) {
            VcsFacadeService.IssueVcsSummary(
                VcsFacadeService.IssueBranchSummary(
                    branches.size, branches.updated
                ), VcsFacadeService.IssueCommitSummary(
                    commits.size, commits.latest
                ), VcsFacadeService.IssuePullRequestSummary(
                    pullRequests.size, pullRequests.status?.let { pullRequestStatus ->
                        VcsFacadeService.IssuePullRequestSummary.Status.valueOf(
                            pullRequestStatus.name
                        )
                    }, pullRequests.updated
                )
            )
        }
    } catch (e: VcsFacadeException) {
        log.error(e.message, e)
        VcsFacadeService.IssueVcsSummary(
            VcsFacadeService.IssueBranchSummary(0, null),
            VcsFacadeService.IssueCommitSummary(0, null),
            VcsFacadeService.IssuePullRequestSummary(0, null, null)
        )
    }

    override fun getCommits(issueKey: String): VcsFacadeService.Repositories<VcsFacadeService.Commit> =
        with(vcsFacadeClient.findCommitsWithFilesByIssueKey(
            issueKey.also { log.info("Get Commits for '{}'", it) }
        )) {
            val repositoryCommits = groupBy { c -> c.commit.repository }
                .map { (repository, commits) ->
                    VcsFacadeService.RepositoryEntities(
                        repository.link,
                        repository.avatar,
                        commits.map { c ->
                            VcsFacadeService.Commit(
                                c.commit.hash,
                                c.commit.link,
                                c.commit.message,
                                c.commit.date,
                                VcsFacadeService.Author(c.commit.author.avatar, c.commit.author.name),
                                c.files.map { f ->
                                    VcsFacadeService.FileChange(
                                        VcsFacadeService.FileChange.Type.valueOf(f.type), f.link, f.path
                                    )
                                }
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
                    VcsFacadeService.IssuePullRequestSummary.Status.valueOf(pr.status.name),
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
        ClassicVcsFacadeClient(vcsFacadeClientParametersProvider).also {
            log.info(
                "Init VCS Facade API client, URL: {}",
                vcsFacadeClientParametersProvider.getApiUrl()
            )
        }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VcsFacadeServiceImpl::class.java)
    }
}
