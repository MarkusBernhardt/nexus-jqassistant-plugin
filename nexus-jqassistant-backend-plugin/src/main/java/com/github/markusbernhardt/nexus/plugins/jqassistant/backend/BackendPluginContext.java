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

import org.slf4j.Logger;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.nexus.configuration.application.ApplicationDirectories;
import org.sonatype.nexus.events.EventSubscriber;
import org.sonatype.nexus.proxy.events.NexusInitializedEvent;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.sisu.goodies.eventbus.EventBus;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.SettingsProvider;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class BackendPluginContext implements EventSubscriber {

	/**
	 * The Logger to use
	 */
	protected final Logger logger;

	/**
	 * The Nexus application directory configuration
	 */
	protected final ApplicationDirectories applicationDirectories;

	/**
	 * The Nexus repository system
	 */
	protected final RepositorySystem repositorySystem;

	/**
	 * The Nexus repository registry
	 */
	protected final RepositoryRegistry repositoryRegistry;

	/**
	 * The Nexus Maven model resolver
	 */
	protected final MavenModelResolver modelResolver;

	/**
	 * The Nexus event bus to which we are posting our events
	 */
	protected final EventBus eventBus;

	/**
	 * The settings provider instance
	 */
	protected final SettingsProvider settingsProvider;

	/**
	 * The Nexus webapp class loader needed when using the nexus-maven-bridge
	 */
	protected ClassLoader webappClassLoader;

	@Inject
	public BackendPluginContext(Logger logger, ApplicationDirectories applicationDirectories, RepositorySystem repositorySystem,
			RepositoryRegistry repositoryRegistry, MavenModelResolver modelResolver, EventBus eventBus, SettingsProvider settingsProvider) throws Exception {
		this.logger = logger;
		this.applicationDirectories = applicationDirectories;
		this.repositorySystem = repositorySystem;
		this.repositoryRegistry = repositoryRegistry;
		this.modelResolver = modelResolver;
		this.eventBus = eventBus;
		this.settingsProvider = settingsProvider;
	}

	@Subscribe
	public void onNexusInitialized(final NexusInitializedEvent evt) {
		webappClassLoader = Thread.currentThread().getContextClassLoader();
	}

	public Logger getLogger() {
		return logger;
	}

	public ApplicationDirectories getApplicationDirectories() {
		return applicationDirectories;
	}

	public RepositorySystem getRepositorySystem() {
		return repositorySystem;
	}

	public RepositoryRegistry getRepositoryRegistry() {
		return repositoryRegistry;
	}

	public MavenModelResolver getModelResolver() {
		return modelResolver;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public SettingsProvider getSettingsProvider() {
		return settingsProvider;
	}

	public ClassLoader getWebappClassLoader() {
		return webappClassLoader;
	}

}
