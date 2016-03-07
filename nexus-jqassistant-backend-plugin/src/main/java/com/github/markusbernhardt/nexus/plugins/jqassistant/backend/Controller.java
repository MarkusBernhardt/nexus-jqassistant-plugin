package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class Controller {

	/**
	 * The plugin context
	 */
	protected final BackendPluginContext backendPluginContext;

	@Inject
	public Controller(BackendPluginContext backendPluginContext) {
		this.backendPluginContext = backendPluginContext;
	}
}
