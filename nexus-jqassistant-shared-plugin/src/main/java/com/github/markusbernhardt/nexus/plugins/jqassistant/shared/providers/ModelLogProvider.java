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

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ModelLogEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.SettingsXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class ModelLogProvider extends AbstractLogProvider<ModelLogXO, ModelLogListXO> implements EventSubscriber {

	@Inject
	public ModelLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings());
	}

	@Subscribe
	public void onModelLog(final ModelLogEvent evt) {
		ModelLogXO modelLogXO = new ModelLogXO();
		modelLogXO.setSequence(sequence.incrementAndGet());
		modelLogXO.setUid(evt.getUid());
		modelLogXO.setTimestamp(timestampFormatter.get().format(evt.getEventDate()));
		modelLogXO.setRepository(evt.getRepository());
		modelLogXO.setGroupId(evt.getGroupId());
		modelLogXO.setArtifactId(evt.getArtifactId());
		modelLogXO.setVersion(evt.getVersion());
		modelLogXO.setFilename(evt.getFilename());
		modelLogXO.setDuration(String.format("%,d ms", evt.getDuration()));

		log.add(modelLogXO);
		trimSize();
	}

	@Override
	protected ModelLogListXO createLogList(int count, int total, List<ModelLogXO> rows) {
		ModelLogListXO logList = new ModelLogListXO();
		logList.setCount(count);
		logList.setTotal(total);
		logList.setRows(rows);
		return logList;
	}

	@Override
	protected int getMaxLogSize(SettingsXO settings) {
		return settings.getModelLogSize();
	}

}
