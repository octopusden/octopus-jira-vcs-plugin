package org.octopusden.octopus.jira.vcs.ui;

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

    public PluginSettingsView(PluginSettings pluginSettings, VcsFacadeService vcsFacadeService) {
        super(pluginSettings);
        this.vcsFacadeService = vcsFacadeService;
    }

    @Override
    public List<PluginProperty> getAvailableSettings() {
        return AVAILABLE_SETTINGS;
    }

    @Override
    public void update() {
        vcsFacadeService.updateConnection();
    }

    @Override
    public Logger getLog() {
        return LOG;
    }
}
