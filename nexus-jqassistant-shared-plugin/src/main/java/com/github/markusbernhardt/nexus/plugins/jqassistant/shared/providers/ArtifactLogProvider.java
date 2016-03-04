package com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ArtifactEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ArtifactLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ArtifactLogXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class ArtifactLogProvider extends AbstractLogProvider<ArtifactLogXO, ArtifactLogListXO> implements EventSubscriber {

	@Inject
	public ArtifactLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings().getArtifactLogSize());
	}

	@Subscribe
	public void onArtifacted(final ArtifactEvent evt) {
		ArtifactLogXO artifactLogXO = new ArtifactLogXO();
		artifactLogXO.setSequence(sequence.incrementAndGet());
		artifactLogXO.setTimestamp(timestampFormatter.get().format(evt.getEventDate()));
		artifactLogXO.setUid(evt.getUid());
		artifactLogXO.setRepository(evt.getRepository());
		artifactLogXO.setGroupId(evt.getGroupId());
		artifactLogXO.setArtifactId(evt.getArtifactId());
		artifactLogXO.setVersion(evt.getVersion());
		artifactLogXO.setClassifier(evt.getClassifier());
		artifactLogXO.setExtension(evt.getExtension());
		artifactLogXO.setFilename(evt.getFilename());
		artifactLogXO.setDuration(String.format("%,d ms", evt.getDuration()));
		artifactLogXO.setFullScan(evt.isFullScan());
		artifactLogXO.setCreatedAt(evt.getCreatedAt() == 0 ? "" : timestampFormatter.get().format(new Date(evt.getCreatedAt())));
		artifactLogXO.setCreatedByAddress(evt.getCreatedByAddress());
		artifactLogXO.setCreatedByUser(evt.getCreatedByUser());
		artifactLogXO.setUpdatedAt(evt.getUpdatedAt() == 0 ? "" : timestampFormatter.get().format(new Date(evt.getUpdatedAt())));
		artifactLogXO.setUpdatedByAddress(evt.getUpdatedByAddress());
		artifactLogXO.setUpdatedByUser(evt.getUpdatedByUser());
		artifactLogXO.setRequestedLastAt(evt.getRequestedLastAt() == 0 ? "" : timestampFormatter.get().format(new Date(evt.getRequestedLastAt())));
		artifactLogXO.setRequestedCount(String.format("%,d", evt.getRequestedCount()));
		artifactLogXO.setDescriptors(formatDescriptors(evt.getDescriptors()));

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
	protected int getNewMaxLogSize(SettingsEvent evt) {
		return evt.getSettingsNew().getArtifactLogSize();
	}

}