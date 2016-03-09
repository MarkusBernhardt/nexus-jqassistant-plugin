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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.buschmais.jqassistant.core.plugin.api.PluginRepositoryException;
import com.buschmais.jqassistant.core.plugin.api.ScannerPluginRepository;
import com.buschmais.jqassistant.core.plugin.api.ScopePluginRepository;
import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.impl.ScannerContextImpl;
import com.buschmais.jqassistant.core.scanner.impl.ScannerImpl;
import com.buschmais.jqassistant.plugin.maven3.api.scanner.ScanInclude;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.Command;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.PluginRepositoryProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.StoreProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner.NexusStorageItemScannerContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner.Util;

public class ScanStorageItemCommand implements Command {

	/**
	 * The plugin context
	 */
	protected final BackendPluginContext backendPluginContext;

	/**
	 * The store provider
	 */
	protected final StoreProvider storeProvider;

	/**
	 * The plugin provider
	 */
	protected final PluginRepositoryProvider pluginRepositoryProvider;

	/**
	 * The storage item to scan
	 */
	protected final StorageItem storageItem;

	/**
	 * The context of the storage item event
	 */
	protected final Map<String, Object> storageItemContext;

	public ScanStorageItemCommand(BackendPluginContext backendPluginContext, StoreProvider storeProvider, PluginRepositoryProvider pluginRepositoryProvider,
			StorageItem storageItem, Map<String, Object> storageItemContext) {
		super();
		this.backendPluginContext = backendPluginContext;
		this.storeProvider = storeProvider;
		this.pluginRepositoryProvider = pluginRepositoryProvider;
		this.storageItem = storageItem;
		this.storageItemContext = storageItemContext;
	}

	@Override
	public void execute() {
		try {
			String storageFilePath = null;
			if (StorageFileItem.class.isAssignableFrom(storageItem.getClass())) {
				StorageFileItem storageFileItem = (StorageFileItem) storageItem;
				storageFilePath = Util.getFileFromStorageFileItem(storageFileItem).getAbsolutePath();
			}

			ScannerContext scannerContext = new ScannerContextImpl(storeProvider.getStore());
			scannerContext.push(NexusStorageItemScannerContext.class, new NexusStorageItemScannerContext(backendPluginContext, storageItemContext));

			ScannerPluginRepository scannerPluginRepository = pluginRepositoryProvider.getScannerPluginRepository();
			List<ScannerPlugin<?, ?>> scannerPlugins = scannerPluginRepository.getScannerPlugins(scannerContext, getPluginProperties());
			ScopePluginRepository scopePluginRepository = pluginRepositoryProvider.getScopePluginRepository();
			Scanner scanner = new ScannerImpl(scannerContext, scannerPlugins, scopePluginRepository.getScopes());

			storeProvider.getStore().beginTransaction();
			try {
				scanner.scan(storageItem, storageFilePath, null);
			} finally {
				storeProvider.getStore().commitTransaction();
			}
		} catch (LocalStorageException | PluginRepositoryException e) {
			backendPluginContext.getLogger().error(e.getLocalizedMessage(), e);
		}
	}

	// TODO Nexus config
	protected List<ScanInclude> scanIncludes;

	// TODO Nexus config
	private Map<String, Object> scanProperties;

	protected Map<String, Object> getPluginProperties() {
		Map<String, Object> properties = new HashMap<>();
		if (scanProperties != null) {
			properties.putAll(scanProperties);
		}
		properties.put(ScanInclude.class.getName(), scanIncludes);
		return properties;
	}

}
