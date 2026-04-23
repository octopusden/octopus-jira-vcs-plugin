package org.octopusden.octopus.jira.vcs.ui;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.web.action.issue.AbstractIssueSelectAction;
import org.apache.velocity.tools.generic.DateTool;
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;

abstract class VcsDialogAction extends AbstractIssueSelectAction {

    private final DateTool dateTool = new DateTool();
    protected final VcsFacadeService vcsFacadeService;

    public VcsDialogAction() {
        this.vcsFacadeService = requireComponent(VcsFacadeService.class);
        final String[] issueKeys = getHttpRequest().getParameterValues("issueKey");
        if (issueKeys != null && issueKeys.length == 1) {
            setKey(issueKeys[0]);
        }
    }

    private static <T> T requireComponent(Class<T> type) {
        T component = ComponentAccessor.getOSGiComponentInstanceOfType(type);
        if (component == null) {
            throw new IllegalStateException(
                    "Unable to resolve OSGi component of type " + type.getName()
                            + ". Check that the Octopus JIRA VCS Plugin is fully enabled"
                            + " and that its components are declared as public in atlassian-plugin.xml.");
        }
        return component;
    }

    @Override
    public String doDefault() throws Exception {
        return getKey() == null ? ERROR : INPUT;
    }

    public DateTool getDateTool() {
        return dateTool;
    }
}
