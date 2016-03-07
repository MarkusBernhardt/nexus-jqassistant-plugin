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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors;

import com.buschmais.jqassistant.plugin.maven3.api.model.MavenDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label(value = "Snapshot")
public interface NexusMavenSnapshotDescriptor extends NexusDescriptor, MavenDescriptor {

	/**
	 * The timestamp of this snapshot.
	 * 
	 * @return the timestamp of this snapshot as long.
	 */
	@Property("timestamp")
	long getTimestamp();

	void setTimestamp(long timestamp);

	/**
	 * The build number of this snapshot.
	 * 
	 * @return the buildnumber of this snapshot as int.
	 */
	@Property("buildnumber")
	int getSnapshotBuildNumber();

	void setSnapshotBuildNumber(int buildnumber);

}