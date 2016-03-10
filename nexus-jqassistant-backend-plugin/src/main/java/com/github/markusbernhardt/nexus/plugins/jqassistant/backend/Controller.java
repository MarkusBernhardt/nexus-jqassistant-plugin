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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;
import org.sonatype.nexus.proxy.events.NexusInitializedEvent;
import org.sonatype.nexus.proxy.events.NexusStoppedEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCache;
import org.sonatype.nexus.proxy.events.RepositoryItemEventRetrieve;

import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands.ScanStorageItemCommand;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands.StoreProviderStart;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands.StoreProviderStop;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.PluginRepositoryProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.StoreProvider;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner.NexusScope;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class Controller implements EventSubscriber {

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
	 * The worker to execute the pending commands
	 */
	protected final CommandQueue commandQueue;

	@Inject
	public Controller(BackendPluginContext backendPluginContext, StoreProvider storeProvider, PluginRepositoryProvider pluginRepositoryProvider,
			CommandQueue commandQueue) {
		this.backendPluginContext = backendPluginContext;
		this.storeProvider = storeProvider;
		this.pluginRepositoryProvider = pluginRepositoryProvider;
		this.commandQueue = commandQueue;
	}

	@Subscribe
	public void onNexusInitialized(final NexusInitializedEvent evt) {
		// always start the command queue
		commandQueue.start();

		// only start the neo4j database, when the plug in is activated
		if (backendPluginContext.getSettingsProvider().getSettings().isActivated()) {
			commandQueue.enqueueCommand(new StoreProviderStart(backendPluginContext, storeProvider));
		}
	}

	@Subscribe
	public void onNexusStopped(final NexusStoppedEvent evt) {
		// only stop the neo4j database, when the plug in is activated
		if (backendPluginContext.getSettingsProvider().getSettings().isActivated()) {
			commandQueue.enqueueCommand(new StoreProviderStop(storeProvider));
		}

		// always stop the command queue
		commandQueue.stop();
	}

	@Subscribe
	public void onSettingsChange(final SettingsEvent evt) {
		if (evt.getSettingsNew().isActivated() && !evt.getSettingsOld().isActivated()) {
			// deactivated => activated => start the neo4j database
			commandQueue.enqueueCommand(new StoreProviderStart(backendPluginContext, storeProvider));
		} else if (!evt.getSettingsNew().isActivated() && evt.getSettingsOld().isActivated()) {
			// activated => deactivated => stop the neo4j database
			commandQueue.enqueueCommand(new StoreProviderStop(storeProvider));
		}
		// Check command queue size
		if (evt.getSettingsNew().getCommandQueueSize() != evt.getSettingsOld().getCommandQueueSize()) {
			commandQueue.resize(evt.getSettingsNew().getCommandQueueSize());
		}
	}

	@Subscribe
	public void onItemCache(final RepositoryItemEventCache evt) {
		if (!backendPluginContext.getSettingsProvider().getSettings().isActivated()) {
			return;
		}

		commandQueue.enqueueCommand(new ScanStorageItemCommand(backendPluginContext, storeProvider, pluginRepositoryProvider, evt.getItem(),
				evt.getItemContext(), NexusScope.SCAN));
	}

	@Subscribe
	public void onItemRetrieve(final RepositoryItemEventRetrieve evt) {
		if (!backendPluginContext.getSettingsProvider().getSettings().isActivated()) {
			return;
		}

		commandQueue.enqueueCommand(
				new ScanStorageItemCommand(backendPluginContext, storeProvider, pluginRepositoryProvider, evt.getItem(), evt.getItemContext(), NexusScope.LOG));
	}

}
