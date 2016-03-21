/*
 * Copyright (C) 2015 Markus Bernhardt
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.github.markusbernhardt.nexus.plugins.jqassistant.frontend;
/*
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.ArtifactLogProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.InformationPanelProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.ModelLogProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.RequestLogProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.SettingsProvider;

@Named
@Singleton
public class FrontendPluginContext {

	protected final Logger logger;

	protected final ArtifactLogProvider artifactLogProvider;

	protected final InformationPanelProvider informationPanelProvider;

	protected final ModelLogProvider modelLogProvider;

	protected final RequestLogProvider requestLogProvider;

	protected final SettingsProvider settingsProvider;

	@Inject
	public FrontendPluginContext(Logger logger, ArtifactLogProvider artifactLogProvider,
			InformationPanelProvider informationPanelProvider, ModelLogProvider modelLogProvider,
			RequestLogProvider requestLogProvider, SettingsProvider settingsProvider) {
		super();
		this.logger = logger;
		this.artifactLogProvider = artifactLogProvider;
		this.informationPanelProvider = informationPanelProvider;
		this.modelLogProvider = modelLogProvider;
		this.requestLogProvider = requestLogProvider;
		this.settingsProvider = settingsProvider;
	}

	public Logger getLogger() {
		return logger;
	}

	public ArtifactLogProvider getArtifactLogProvider() {
		return artifactLogProvider;
	}

	public InformationPanelProvider getInformationPanelProvider() {
		return informationPanelProvider;
	}

	public ModelLogProvider getModelLogProvider() {
		return modelLogProvider;
	}

	public RequestLogProvider getRequestLogProvider() {
		return requestLogProvider;
	}

	public SettingsProvider getSettingsProvider() {
		return settingsProvider;
	}

}
