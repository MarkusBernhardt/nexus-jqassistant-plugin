package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;
import org.sonatype.nexus.proxy.events.NexusInitializedEvent;
import org.sonatype.nexus.proxy.events.NexusStoppedEvent;

import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands.StoreProviderStart;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.commands.StoreProviderStop;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers.StoreProvider;
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
	 * The worker to execute the pending commands
	 */
	protected final CommandQueue commandQueue;

	@Inject
	public Controller(BackendPluginContext backendPluginContext, StoreProvider storeProvider, CommandQueue commandQueue) {
		this.backendPluginContext = backendPluginContext;
		this.storeProvider = storeProvider;
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
}
