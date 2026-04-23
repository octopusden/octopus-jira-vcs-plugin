package org.octopusden.octopus.jira.vcs.ui;

import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

public class CommitsDialogAction extends VcsDialogAction {

    VcsFacadeService.Repositories<VcsFacadeService.Commit> commits;

    public CommitsDialogAction() {
        super();
    }

    public VcsFacadeService.Repositories<VcsFacadeService.Commit> getRepositoryCommits() {
        if (commits == null) {
            commits = vcsFacadeService.getCommits(getId());
        }
        return commits;
    }
}
