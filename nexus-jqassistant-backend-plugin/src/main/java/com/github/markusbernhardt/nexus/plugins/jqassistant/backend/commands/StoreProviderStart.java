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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands;

import com.buschmais.jqassistant.core.plugin.api.PluginRepositoryException;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.Command;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.StoreProvider;

public class StoreProviderStart implements Command {

	/**
	 * The plugin context
	 */
	protected final BackendPluginContext backendPluginContext;

	/**
	 * The store provider
	 */
	protected final StoreProvider storeProvider;

	public StoreProviderStart(BackendPluginContext backendPluginContext, StoreProvider storeProvider) {
		super();
		this.backendPluginContext = backendPluginContext;
		this.storeProvider = storeProvider;
	}

	@Override
	public void execute() {
		try {
			storeProvider.start();
		} catch (PluginRepositoryException e) {
			backendPluginContext.getLogger().error("Cannot start JQA store", e);
		}
	}

}
