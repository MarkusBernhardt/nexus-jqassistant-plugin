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

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label(value = "StorageItem")
public interface NexusStorageItemDescriptor extends NexusDescriptor {

	@Property("createdAt")
	long getCreatedAt();

	void setCreatedAt(long createdAt);

	@Property("createdByAddress")
	String getCreatedByAddress();

	void setCreatedByAddress(String createdByAddress);

	@Property("createdByUser")
	String getCreatedByUser();

	void setCreatedByUser(String createdByUser);

	@Property("lastUpdatedAt")
	long getLastUpdatedAt();

	void setLastUpdatedAt(long lastUpdatedAt);

	@Property("lastUpdatedByAddress")
	String getLastUpdatedByAddress();

	void setLastUpdatedByAddress(String lastUpdatedByAddress);

	@Property("lastUpdatedByUser")
	String getLastUpdatedByUser();

	void setLastUpdatedByUser(String lastUpdatedByUser);

	@Property("lastRequestedAt")
	long getLastRequestedAt();

	void setLastRequestedAt(long lastRequestedAt);

	@Property("lastRequestedByAddress")
	String getLastRequestedByAddress();

	void setLastRequestedByAddress(String lastRequestedByAddress);

	@Property("lastRequestedByUser")
	String getLastRequestedByUser();

	void setLastRequestedByUser(String lastRequestedByUser);

	@Property("requestCount")
	long getRequestCount();

	void setRequestCount(long requestCount);

}
