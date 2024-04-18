package org.octopusden.octopus.jira.vcs.ui;

import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

public class BranchesDialogAction extends VcsDialogAction {

    private VcsFacadeService.Repositories<VcsFacadeService.Branch> branches;

    public BranchesDialogAction(VcsFacadeService vcsFacadeService) {
        super(vcsFacadeService);
    }

    public VcsFacadeService.Repositories<VcsFacadeService.Branch> getRepositoryBranches() {
        if (branches == null) {
            branches = vcsFacadeService.getBranches(getKey());
        }
        return branches;
    }
}
