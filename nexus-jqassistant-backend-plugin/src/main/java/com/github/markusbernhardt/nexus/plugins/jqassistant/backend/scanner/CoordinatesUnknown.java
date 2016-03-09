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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner;

import org.sonatype.nexus.proxy.maven.gav.Gav;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;

public class CoordinatesUnknown implements Coordinates {

	protected final String group;

	protected final String name;

	protected final String version;

	protected final String classifier;

	protected final String type;

	public CoordinatesUnknown(Coordinates coordinates) {
		super();
		String group = coordinates.getGroup();
		if (group == null) {
			group = "unknown";
		}
		this.group = group;

		String name = coordinates.getName();
		if (name == null) {
			name = "unknown";
		}
		this.name = name;

		String version = coordinates.getVersion();
		if (version == null) {
			version = "unknown";
		}
		this.version = version;

		this.classifier = coordinates.getClassifier();
		this.type = coordinates.getType();
	}

	public CoordinatesUnknown(Gav gav) {
		super();
		String group = gav.getGroupId();
		if (group == null) {
			group = "unknown";
		}
		this.group = group;

		String name = gav.getArtifactId();
		if (name == null) {
			name = "unknown";
		}
		this.name = name;

		String version = gav.getVersion();
		if (version == null) {
			version = "unknown";
		}
		this.version = version;

		this.classifier = gav.getClassifier();
		this.type = gav.getExtension();
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassifier() {
		return classifier;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
