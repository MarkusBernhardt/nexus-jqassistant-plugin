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
package com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events;

import org.sonatype.nexus.events.AbstractEvent;

public class ModelEvent extends AbstractEvent<Object> {

	protected final String uid;
	protected final String repository;
	protected final String groupId;
	protected final String artifactId;
	protected final String version;
	protected final String filename;
	protected final long duration;

	public ModelEvent(Object component, String uid, String repository, String groupId, String artifactId, String version, String filename, long duration) {
		super(component);
		this.uid = uid;
		this.repository = repository;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.filename = filename;
		this.duration = duration;
	}

	public String getUid() {
		return uid;
	}

	public String getRepository() {
		return repository;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getFilename() {
		return filename;
	}

	public long getDuration() {
		return duration;
	}

}
