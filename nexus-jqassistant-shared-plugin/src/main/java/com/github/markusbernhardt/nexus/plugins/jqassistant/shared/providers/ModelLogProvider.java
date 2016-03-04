package com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ArtifactEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class ModelLogProvider extends AbstractLogProvider<ModelLogXO, ModelLogListXO> implements EventSubscriber {

	@Inject
	public ModelLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings().getModelLogSize());
	}

	@Subscribe
	public void onArtifacted(final ArtifactEvent evt) {
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
	protected int getNewMaxLogSize(SettingsEvent evt) {
		return evt.getSettingsNew().getModelLogSize();
	}

}
