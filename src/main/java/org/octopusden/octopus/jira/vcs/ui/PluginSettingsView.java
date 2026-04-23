package org.octopusden.octopus.jira.vcs.ui;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import org.octopusden.octopus.jira.vcs.config.PluginProperty;
import org.octopusden.octopus.jira.vcs.config.PluginSettings;
import org.octopusden.octopus.jira.vcs.integration.vcsfacade.VcsFacadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@WebSudoRequired
public class PluginSettingsView extends AbstractViewSettings {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractViewSettings.class);
    private static final List<PluginProperty> AVAILABLE_SETTINGS = new ArrayList<>(PluginProperty.getEntries());

    private final VcsFacadeService vcsFacadeService;

    public PluginSettingsView() {
        super(requireComponent(PluginSettings.class));
        this.vcsFacadeService = requireComponent(VcsFacadeService.class);
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
    public List<PluginProperty> getAvailableSettings() {
        return AVAILABLE_SETTINGS;
    }

    @Override
    public void update() {
        vcsFacadeService.updateProperties();
    }

    @Override
    public Logger getLog() {
        return LOG;
    }
}
