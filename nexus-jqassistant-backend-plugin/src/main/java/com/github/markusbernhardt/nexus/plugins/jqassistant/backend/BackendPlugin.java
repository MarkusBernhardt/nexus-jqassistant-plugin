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

import java.io.File;
import java.util.ResourceBundle;

import javax.inject.Named;

import org.eclipse.sisu.EagerSingleton;
import org.jetbrains.annotations.NonNls;
import org.sonatype.nexus.plugin.PluginIdentity;

@Named
@EagerSingleton
public class BackendPlugin extends PluginIdentity {

	/**
	 * Prefix for ID-like things.
	 */
	@NonNls
	public static final String ID_PREFIX = "jqassistant-backend";

	/**
	 * Expected groupId for plugin artifact.
	 */
	@NonNls
	public static final String PLUGIN_GROUP_ID = "com.github.markusbernhardt.nexus.plugins";

	/**
	 * Expected artifactId for plugin artifact.
	 */
	@NonNls
	public static final String PLUGIN_ARTIFACT_ID = "nexus-" + ID_PREFIX + "-plugin";

	/**
	 * The actual version of this plugin. Loaded from maven project.
	 */
	@NonNls
	public static final String PLUGIN_VERSION = loadBundleProperty("version");

	/**
	 * Prefix for REST resources
	 */
	@NonNls
	public static final String REST_PREFIX = "/jqassistant";

	/**
	 * jQAssistant permission.
	 */
	@NonNls
	public static final String PERMISSION = "nexus:jqassistant";

	/**
	 * Default constructor.
	 * 
	 * @throws Exception
	 */
	public BackendPlugin() throws Exception {
		super(PLUGIN_GROUP_ID, PLUGIN_ARTIFACT_ID);

		assert PLUGIN_ARTIFACT_ID.equals(loadBundleProperty("artifactId")) : PLUGIN_ARTIFACT_ID;
		assert PLUGIN_GROUP_ID.equals(loadBundleProperty("groupId")) : PLUGIN_GROUP_ID;
	}

	protected static String loadBundleProperty(String propertyName) {
		try {
			return ResourceBundle.getBundle(BackendPlugin.class.getCanonicalName().replace(".", File.separator)).getString(propertyName);
		} catch (RuntimeException e) {
			return "unknown";
		}
	}
}
