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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.proxy.maven.maven2.M2Repository;

import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner.Util;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.InformationPanelUpdateRequestXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.InformationPanelXO;
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
	public InformationPanelXO getInformationPanel(InformationPanelUpdateRequestXO informationPanelUpdateRequest) {
		InformationPanelXO informationPanel = new InformationPanelXO();
		informationPanel.setActivated(backendPluginContext.getSettingsProvider().getSettings().isActivated());

		backendPluginContext.getLogger().error(informationPanelUpdateRequest.getResourceURI());

		List<M2Repository> repositories = Util.getM2RepositoryList(backendPluginContext.getRepositoryRegistry());
		for (M2Repository repository : repositories) {
			backendPluginContext.getLogger().error(repository.getLocalUrl());
			backendPluginContext.getLogger().error(repository.getRemoteUrl());
		}

		return informationPanel;
	}

}
