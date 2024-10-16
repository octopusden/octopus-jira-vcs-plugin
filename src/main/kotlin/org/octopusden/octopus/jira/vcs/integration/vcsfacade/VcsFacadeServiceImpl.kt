package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import com.atlassian.jira.issue.IssueManager
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import java.util.Date
import javax.inject.Named
import org.octopusden.octopus.jira.vcs.config.PluginProperty
import org.octopusden.octopus.jira.vcs.config.PluginSettings
import org.octopusden.octopus.vcsfacade.client.VcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.ClassicVcsFacadeClient
import org.octopusden.octopus.vcsfacade.client.impl.VcsFacadeClientParametersProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Named
class VcsFacadeServiceImpl(
    @ComponentImport private val issueManager: IssueManager,
    private val vcsFacadeClientParametersProvider: VcsFacadeClientParametersProvider,
    private val pluginSettings: PluginSettings
) : VcsFacadeService {

    private var vcsFacadeClient: VcsFacadeClient = getClient()
    private var commitFileLimit = pluginSettings.getLong(PluginProperty.VCS_PANEL_COMMIT_FILE_LIMIT).toInt()

    override fun updateProperties() {
        vcsFacadeClient = getClient()
        commitFileLimit = pluginSettings.getLong(PluginProperty.VCS_PANEL_COMMIT_FILE_LIMIT).toInt()
    }

    override fun getSummary(issueId: Long): VcsFacadeService.IssueVcsSummary = try {
        with(vcsFacadeClient.findByIssueKeys(issueManager.getAllIssueKeys(issueId).also {
            log.info("Get VCS Summary for issue with id $issueId $it")
        })) {
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
    } catch (e: Exception) {
        log.error(e.message, e)
        VcsFacadeService.IssueVcsSummary(
            VcsFacadeService.IssueBranchSummary(0, null),
            VcsFacadeService.IssueCommitSummary(0, null),
            VcsFacadeService.IssuePullRequestSummary(0, null, null)
        )
    }

    override fun getCommits(issueId: Long): VcsFacadeService.Repositories<VcsFacadeService.Commit> =
        with(vcsFacadeClient.findCommitsWithFilesByIssueKeys(issueManager.getAllIssueKeys(issueId).also {
            log.info("Get Commits for issue with id $issueId $it")
        }, commitFileLimit)) {
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
                                c.totalFiles,
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

    override fun getPullRequests(issueId: Long): Collection<VcsFacadeService.PullRequest> =
        with(vcsFacadeClient.findPullRequestsByIssueKeys(issueManager.getAllIssueKeys(issueId).also {
            log.info("Get Pull Requests for issue with id $issueId $it")
        })) {
            this.map { pr ->
                VcsFacadeService.PullRequest(
                    pr.link,
                    pr.title,
                    VcsFacadeService.Author(pr.author.avatar, pr.author.name),
                    pr.reviewers.map { r -> VcsFacadeService.Reviewer(r.user.name, r.user.avatar, r.approved) }
                        .sortedBy { r -> r.approved },
                    VcsFacadeService.IssuePullRequestSummary.Status.valueOf(pr.status.name),
                    pr.updatedAt,
                    pr.target
                )
            }
        }

    override fun getBranches(issueId: Long): VcsFacadeService.Repositories<VcsFacadeService.Branch> =
        with(vcsFacadeClient.findBranchesByIssueKeys(issueManager.getAllIssueKeys(issueId).also {
            log.info("Get Branches for issue with id $issueId $it")
        })) {
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
