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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.siesta.common.Resource;

import com.github.markusbernhardt.nexus.plugins.jqassistant.frontend.FrontendPlugin;
import com.github.markusbernhardt.nexus.plugins.jqassistant.frontend.FrontendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogListXO;

@Named
@Singleton
@Path(ModelLogResource.RESOURCE_URI)
public class ModelLogResource extends ComponentSupport implements Resource {

	public static final String RESOURCE_URI = FrontendPlugin.REST_PREFIX + "/model-log";

	/**
	 * The plug in context
	 */
	protected final FrontendPluginContext frontendPluginContext;

	@Inject
	public ModelLogResource(FrontendPluginContext frontendPluginContext) {
		super();
		this.frontendPluginContext = frontendPluginContext;
	}

	@GET
	@Produces({ APPLICATION_JSON, APPLICATION_XML })
	@RequiresPermissions(FrontendPlugin.PERMISSION)
	public ModelLogListXO get(@QueryParam("start") @javax.ws.rs.DefaultValue("0") int start, @QueryParam("limit") @javax.ws.rs.DefaultValue("-1") int limit) {
		return frontendPluginContext.getModelLogProvider().getLogList(start, limit);
	}

	@DELETE
	@Produces({ APPLICATION_JSON, APPLICATION_XML })
	@RequiresPermissions(FrontendPlugin.PERMISSION)
	public void delete() {
		frontendPluginContext.getModelLogProvider().clear();
	}

}