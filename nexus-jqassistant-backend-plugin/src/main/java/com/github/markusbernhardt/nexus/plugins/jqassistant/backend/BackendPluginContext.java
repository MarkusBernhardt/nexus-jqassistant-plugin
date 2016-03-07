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
import org.sonatype.nexus.configuration.application.ApplicationDirectories;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers.SettingsProvider;

@Named
@Singleton
public class BackendPluginContext {

	/**
	 * The Logger to use
	 */
	protected final Logger logger;

	/**
	 * The Nexus application directory configuration
	 */
	protected final ApplicationDirectories applicationDirectories;

	/**
	 * The settings provider instance
	 */
	protected final SettingsProvider settingsProvider;

	@Inject
	public BackendPluginContext(Logger logger, ApplicationDirectories applicationDirectories, SettingsProvider settingsProvider) throws Exception {
		this.logger = logger;
		this.applicationDirectories = applicationDirectories;
		this.settingsProvider = settingsProvider;
	}

	public Logger getLogger() {
		return logger;
	}

	public ApplicationDirectories getApplicationDirectories() {
		return applicationDirectories;
	}

	public SettingsProvider getSettingsProvider() {
		return settingsProvider;
	}

}
