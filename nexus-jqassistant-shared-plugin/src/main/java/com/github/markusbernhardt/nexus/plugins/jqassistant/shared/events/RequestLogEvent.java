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

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogXO;

public class RequestLogEvent extends AbstractEvent<Object> {

	protected final RequestLogXO requestLogXO;

	public RequestLogEvent(Object component, RequestLogXO requestLogXO) {
		super(component);
		this.requestLogXO = requestLogXO;
	}

	public RequestLogXO getRequestLogXO() {
		return requestLogXO;
	}

}
