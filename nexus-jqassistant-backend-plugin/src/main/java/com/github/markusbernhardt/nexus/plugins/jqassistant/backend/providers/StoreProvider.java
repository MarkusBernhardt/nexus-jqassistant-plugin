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

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.buschmais.jqassistant.core.plugin.api.PluginRepositoryException;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.impl.EmbeddedGraphStore;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;

@Named
@Singleton
public class StoreProvider {

	public static final String DB_LOCATION = "db/jqa";

	/**
	 * The create jQAssistant (Neo4j) store instance
	 */
	protected final Store store;

	/**
	 * The plugin repository provider instance
	 */
	protected final PluginRepositoryProvider pluginRepositoryProvider;

	@Inject
	StoreProvider(BackendPluginContext backendPluginContext, PluginRepositoryProvider pluginRepositoryProvider) {
		File databaseDirectory = backendPluginContext.getApplicationDirectories().getWorkDirectory(DB_LOCATION);
		this.pluginRepositoryProvider = pluginRepositoryProvider;
		this.store = new EmbeddedGraphStore(databaseDirectory.getAbsolutePath());
	}

	public Store getStore() {
		return store;
	}

	public void start() throws PluginRepositoryException {
		store.start(pluginRepositoryProvider.getModelPluginRepository().getDescriptorTypes());
	}

	public void stop() {
		store.stop();
	}
}
