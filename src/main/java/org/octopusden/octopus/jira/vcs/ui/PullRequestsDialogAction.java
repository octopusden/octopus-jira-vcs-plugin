package org.octopusden.octopus.jira.vcs.ui;

import java.util.Collection;
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

public class PullRequestsDialogAction extends VcsDialogAction {

    private Collection<VcsFacadeService.PullRequest> pullRequests;

    public PullRequestsDialogAction(VcsFacadeService vcsFacadeService) {
        super(vcsFacadeService);
    }

    public Collection<VcsFacadeService.PullRequest> getPullRequests() {
        if (pullRequests == null) {
            pullRequests = vcsFacadeService.getPullRequests(getId());
        }
        return pullRequests;
    }
}
