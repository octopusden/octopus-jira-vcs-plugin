package org.octopusden.octopus.jira.vcs.ui;

import com.atlassian.jira.web.action.issue.AbstractIssueSelectAction;
import org.apache.velocity.tools.generic.DateTool;
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

abstract class VcsDialogAction extends AbstractIssueSelectAction {

    private final DateTool dateTool = new DateTool();
    protected final VcsFacadeService vcsFacadeService;

    public VcsDialogAction(VcsFacadeService vcsFacadeService) {
        this.vcsFacadeService = vcsFacadeService;
        final String[] issueKeys = getHttpRequest().getParameterValues("issueKey");
        if (issueKeys != null && issueKeys.length == 1) {
            setKey(issueKeys[0]);
        }
    }

    @Override
    public String doDefault() throws Exception {
        return getKey() == null ? ERROR : INPUT;
    }

    public DateTool getDateTool() {
        return dateTool;
    }
}
