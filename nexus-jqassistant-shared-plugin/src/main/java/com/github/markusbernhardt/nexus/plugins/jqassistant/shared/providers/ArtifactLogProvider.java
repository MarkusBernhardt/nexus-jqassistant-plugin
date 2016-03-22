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

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ArtifactLogEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ArtifactLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ArtifactLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.SettingsXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class ArtifactLogProvider extends AbstractLogProvider<ArtifactLogXO, ArtifactLogListXO>
		implements EventSubscriber {

	@Inject
	public ArtifactLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings());
	}

	@Subscribe
	public void onArtifactLog(final ArtifactLogEvent evt) {
		ArtifactLogXO artifactLogXO = evt.getArtifactLogXO();
		artifactLogXO.setSequence(sequence.incrementAndGet());
		artifactLogXO.setTimestamp(timestampFormatter.get().format(evt.getEventDate()));

		log.add(artifactLogXO);
		trimSize();
	}

	@Override
	protected ArtifactLogListXO createLogList(int count, int total, List<ArtifactLogXO> rows) {
		ArtifactLogListXO logList = new ArtifactLogListXO();
		logList.setCount(count);
		logList.setTotal(total);
		logList.setRows(rows);
		return logList;
	}

	@Override
	protected int getMaxLogSize(SettingsXO settings) {
		return settings.getArtifactLogSize();
	}

}
