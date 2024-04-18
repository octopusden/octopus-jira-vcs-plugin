package org.octopusden.octopus.jira.vcs.ui;

import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

public class CommitsDialogAction extends VcsDialogAction {

    VcsFacadeService.Repositories<VcsFacadeService.Commit> commits;

    public CommitsDialogAction(VcsFacadeService vcsFacadeService) {
        super(vcsFacadeService);
    }

    public VcsFacadeService.Repositories<VcsFacadeService.Commit> getRepositoryCommits() {
        if (commits == null) {
            commits = vcsFacadeService.getCommits(getKey());
        }
        return commits;
    }
}
