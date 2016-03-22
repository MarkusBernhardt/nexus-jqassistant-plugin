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
package com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.RequestLogEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.SettingsXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class RequestLogProvider extends AbstractLogProvider<RequestLogXO, RequestLogListXO> implements EventSubscriber {

	@Inject
	public RequestLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings());
	}

	@Subscribe
	public void onRequestLog(final RequestLogEvent evt) {
		RequestLogXO requestLogXO = evt.getRequestLogXO();
		requestLogXO.setSequence(sequence.incrementAndGet());
		requestLogXO.setTimestamp(timestampFormatter.get().format(evt.getEventDate()));

		log.add(requestLogXO);
		trimSize();
	}

	@Override
	protected RequestLogListXO createLogList(int count, int total, List<RequestLogXO> rows) {
		RequestLogListXO logList = new RequestLogListXO();
		logList.setCount(count);
		logList.setTotal(total);
		logList.setRows(rows);
		return logList;
	}

	@Override
	protected int getMaxLogSize(SettingsXO settings) {
		return settings.getRequestLogSize();
	}

}
