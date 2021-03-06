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

import java.util.List;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label(value = "Repository")
public interface NexusRepositoryDescriptor extends Descriptor, NexusDescriptor {

	/**
	 * The contained artifacts.
	 */
	@Relation("CONTAINS_ITEM")
	List<NexusStorageItemDescriptor> getContainedItems();

	/**
	 * The repository id.
	 * 
	 * @return the repository id.
	 */
	@Indexed
	String getId();

	/**
	 * Set the repository id.
	 * 
	 * @param url
	 *            the repository id.
	 */
	void setId(String id);

	/**
	 * The last full scan.
	 * 
	 * @return the full scan.
	 */
	@Property("lastFullScan")
	long getLastFullScan();

	/**
	 * Set the full scan.
	 * 
	 * @param lastFullScan
	 *            the full scan.
	 */
	void setLastFullScan(long lastFullScan);

}
