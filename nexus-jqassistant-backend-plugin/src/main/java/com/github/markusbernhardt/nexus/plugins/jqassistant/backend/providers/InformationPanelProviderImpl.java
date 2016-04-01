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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.InformationPanelXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.Maven2ArtifactInfoResourceXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.InformationPanelProvider;

@Named
@Singleton
public class InformationPanelProviderImpl implements InformationPanelProvider {

	/**
	 * The settings provider instance
	 */
	protected final BackendPluginContext backendPluginContext;

	@Inject
	InformationPanelProviderImpl(BackendPluginContext backendPluginContext) {
		this.backendPluginContext = backendPluginContext;
	}

	@Override
	public InformationPanelXO getInformationPanel(Maven2ArtifactInfoResourceXO maven2ArtifactInfoResourceXO) {
		InformationPanelXO informationPanel = new InformationPanelXO();
		informationPanel.setActivated(backendPluginContext.getSettingsProvider().getSettings().isActivated());
		return informationPanel;
	}

}
