package org.octopusden.octopus.jira.vcs.ui;

import com.atlassian.jira.web.ExecutingHttpRequest;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.octopusden.octopus.jira.vcs.config.PluginProperty;
import org.octopusden.octopus.jira.vcs.config.PluginSettings;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class AbstractViewSettings extends JiraWebActionSupport {
    protected final PluginSettings pluginSettings;
    private boolean updated = false;

    public AbstractViewSettings(PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public abstract List<PluginProperty> getAvailableSettings();

    public abstract void update();

    public abstract Logger getLog();

    public PluginSettings getPluginSettings() {
        return pluginSettings;
    }

    public boolean isUpdated() {
        return updated;
    }

    @Override
    public String doDefault() {
        final HttpServletRequest request = ExecutingHttpRequest.get();
        applySettings(request);
        return SUCCESS;
    }

    private void applySettings(HttpServletRequest request) {
        if (HttpMethod.POST.name().equals(request.getMethod())) {
            getLog().error("Update Plugin Settings");
            for (PluginProperty property : getAvailableSettings()) {
                final String requestValue = request.getParameter(property.getKey());
                final String newValue = requestValue != null ? requestValue : "";
                updated = updated || !newValue.equals(pluginSettings.getString(property));
                pluginSettings.set(property, newValue);
            }
        }

        if (isUpdated()) {
            update();
        }
    }
}
