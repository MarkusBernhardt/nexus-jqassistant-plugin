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

public class ArtifactLogEvent extends AbstractEvent<Object> {

	protected final String uid;
	protected final String repository;
	protected final String groupId;
	protected final String artifactId;
	protected final String version;
	protected final String classifier;
	protected final String extension;
	protected final String filename;
	protected final long duration;
	protected final boolean fullScan;
	protected final long createdAt;
	protected final String createdByAddress;
	protected final String createdByUser;
	protected final long updatedAt;
	protected final String updatedByAddress;
	protected final String updatedByUser;
	protected final long requestedLastAt;
	protected final long requestedCount;
	protected final String descriptors;

	public ArtifactLogEvent(Object component, String uid, String repository, String groupId, String artifactId, String version, String classifier,
			String extension, String filename, long duration, boolean fullScan, long createdAt, String createdByAddress, String createdByUser, long updatedAt,
			String updatedByAddress, String updatedByUser, long requestedLastAt, long requestedCount, String descriptors) {
		super(component);
		this.uid = uid;
		this.repository = repository;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classifier = classifier;
		this.extension = extension;
		this.filename = filename;
		this.duration = duration;
		this.fullScan = fullScan;
		this.createdAt = createdAt;
		this.createdByAddress = createdByAddress;
		this.createdByUser = createdByUser;
		this.updatedAt = updatedAt;
		this.updatedByAddress = updatedByAddress;
		this.updatedByUser = updatedByUser;
		this.requestedLastAt = requestedLastAt;
		this.requestedCount = requestedCount;
		this.descriptors = descriptors;
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

	public String getClassifier() {
		return classifier;
	}

	public String getExtension() {
		return extension;
	}

	public String getFilename() {
		return filename;
	}

	public long getDuration() {
		return duration;
	}

	public boolean isFullScan() {
		return fullScan;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public String getCreatedByAddress() {
		return createdByAddress;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedByAddress() {
		return updatedByAddress;
	}

	public String getUpdatedByUser() {
		return updatedByUser;
	}

	public long getRequestedLastAt() {
		return requestedLastAt;
	}

	public long getRequestedCount() {
		return requestedCount;
	}

	public String getDescriptors() {
		return descriptors;
	}

}
