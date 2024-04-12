package org.octopusden.octopus.jira.vcs.ui;

import com.atlassian.jira.web.action.issue.AbstractIssueSelectAction;
import org.apache.velocity.tools.generic.DateTool;
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class VcsDialogAction extends AbstractIssueSelectAction {

    public static final Logger LOG = LoggerFactory.getLogger(VcsDialogAction.class);
    private final String issueKey;
    private final DateTool dateTool = new DateTool();
    private final VcsFacadeService vcsFacadeService;

    public VcsDialogAction(VcsFacadeService vcsFacadeService) {
        this.vcsFacadeService = vcsFacadeService;
        final String[] issueKeys = getHttpRequest().getParameterValues("issueKey");
        if (issueKeys.length > 0) {
            issueKey = issueKeys[0];
        } else {
            issueKey = null;
        }
    }

    @Override
    public String doDefault() throws Exception {
        return INPUT;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public DateTool getDateTool() {
        return dateTool;
    }

    public VcsFacadeService.Repositories<VcsFacadeService.Commit> getRepositoryCommits() {
        LOG.info("Get Commits for '{}'", issueKey);
        return vcsFacadeService.getCommits(issueKey);
    }

    public VcsFacadeService.Repositories<VcsFacadeService.Branch> getRepositoryBranches() {
        LOG.info("Get Branches for '{}'", issueKey);
        return vcsFacadeService.getBranches(issueKey);
    }

    public Collection<VcsFacadeService.PullRequest> getPullRequests() {
        LOG.info("Get Pull Requests for '{}'", issueKey);
        return vcsFacadeService.getPullRequests(issueKey);
    }
}
