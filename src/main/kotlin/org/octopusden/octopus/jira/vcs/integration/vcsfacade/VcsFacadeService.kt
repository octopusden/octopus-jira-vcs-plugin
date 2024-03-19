package org.octopusden.octopus.jira.vcs.integration.vcsfacade

import java.util.Date

interface VcsFacadeService {
    fun updateConnection()
    fun getSummary(issueKey: String): IssueVcsSummary
    fun getCommits(issueKey: String): Repositories<Commit>
    fun getPullRequests(issueKey: String): Collection<PullRequest>
    fun getBranches(issueKey: String): Repositories<Branch>

    data class IssueVcsSummary(
        val branches: IssueBranchSummary, val commits: IssueCommitSummary, val pullRequests: IssuePullRequestSummary
    )

    data class IssueBranchSummary(val size: Int, val updated: Date?)
    data class IssuePullRequestSummary(val size: Int, val status: PullRequestStatus?, val updated: Date?) {
        enum class PullRequestStatus(val style: String) {
            OPEN("info"),
            MERGED("success"),
            DECLINED("error")
        }
    }

    data class IssueCommitSummary(val size: Int, val latest: Date?)
    data class Commit(val sha: String, val url: String, val message: String, val date: Date, val author: Author)
    data class Author(val avatar: String?, val name: String)
    data class RepositoryEntities<T : Any>(val url: String, val avatar: String?, val entities: Collection<T>) {
        val name: String
            get() = url.substring(url.lastIndexOf("/") + 1, url.indexOf(".git"))
    }

    data class Repositories<T : Any>(val size: Int, val values: Collection<RepositoryEntities<T>>)
    data class Reviewer(val name: String, val avatar: String?, val approved: Boolean)
    data class Branch(val name: String, val url: String, val updated: Date)

    data class PullRequest(
        val id: Long,
        val url: String,
        val title: String,
        val author: Author,
        val reviewers: Collection<Reviewer>,
        val status: IssuePullRequestSummary.PullRequestStatus,
        val updated: Date,
        val target: String
    )
}
