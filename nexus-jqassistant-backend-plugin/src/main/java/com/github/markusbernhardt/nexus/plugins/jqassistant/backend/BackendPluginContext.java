package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Named
@Singleton
public class BackendPluginContext {

	/**
	 * The Logger to use
	 */
	protected final Logger logger;

	@Inject
	public BackendPluginContext(Logger logger) throws Exception {
		this.logger = logger;
	}

	public Logger getLogger() {
		return logger;
	}

}
