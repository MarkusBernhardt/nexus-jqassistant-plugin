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
package com.github.markusbernhardt.nexus.plugins.jqassistant.frontend.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.siesta.common.Resource;

import com.github.markusbernhardt.nexus.plugins.jqassistant.frontend.FrontendPlugin;
import com.github.markusbernhardt.nexus.plugins.jqassistant.frontend.FrontendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.InformationPanelXO;

@Named
@Singleton
@Path(InformationPanelResource.RESOURCE_URI)
public class InformationPanelResource extends ComponentSupport implements Resource {

	public static final String RESOURCE_URI = FrontendPlugin.REST_PREFIX + "/information-panel";

	/**
	 * The plug in context
	 */
	protected final FrontendPluginContext frontendPluginContext;

	@Inject
	public InformationPanelResource(FrontendPluginContext frontendPluginContext) {
		super();
		this.frontendPluginContext = frontendPluginContext;
	}

	/**
	 * Returns the actual settings.
	 */
	@GET
	@Produces({ APPLICATION_JSON, APPLICATION_XML })
	@RequiresPermissions(FrontendPlugin.PERMISSION)
	public synchronized InformationPanelXO get() {
		return frontendPluginContext.getInformationPanelProvider().getInformationPanel();

	}
}