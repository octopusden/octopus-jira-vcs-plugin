package org.octopusden.octopus.jira.vcs.ui;

import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

import java.util.Collection;

public class PullRequestsDialogAction extends VcsDialogAction {

    private Collection<VcsFacadeService.PullRequest> pullRequests;

    public PullRequestsDialogAction(VcsFacadeService vcsFacadeService) {
        super(vcsFacadeService);
    }

    public Collection<VcsFacadeService.PullRequest> getPullRequests() {
        if (pullRequests == null) {
            pullRequests = vcsFacadeService.getPullRequests(getKey());
        }
        return pullRequests;
    }
}
