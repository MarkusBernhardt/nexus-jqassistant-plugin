package com.github.markusbernhardt.nexus.plugins.jqassistant.shared.providers;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ArtifactEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogListXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogXO;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
public class RequestLogProvider extends AbstractLogProvider<RequestLogXO, RequestLogListXO> implements EventSubscriber {

	@Inject
	public RequestLogProvider(SettingsProvider settingsProvider) {
		super(settingsProvider.getSettings().getRequestLogSize());
	}

	@Subscribe
	public void onArtifacted(final ArtifactEvent evt) {
		RequestLogXO requestLogXO = new RequestLogXO();
		requestLogXO.setSequence(sequence.incrementAndGet());
		requestLogXO.setTimestamp(timestampFormatter.get().format(evt.getEventDate()));
		requestLogXO.setUid(evt.getUid());
		requestLogXO.setRepository(evt.getRepository());
		requestLogXO.setGroupId(evt.getGroupId());
		requestLogXO.setArtifactId(evt.getArtifactId());
		requestLogXO.setVersion(evt.getVersion());
		requestLogXO.setClassifier(evt.getClassifier());
		requestLogXO.setExtension(evt.getExtension());
		requestLogXO.setFilename(evt.getFilename());
		requestLogXO.setRequestedLastAt(evt.getRequestedLastAt() == 0 ? "" : timestampFormatter.get().format(new Date(evt.getRequestedLastAt())));
		requestLogXO.setRequestedCount(String.format("%,d", evt.getRequestedCount()));

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
	protected int getNewMaxLogSize(SettingsEvent evt) {
		return evt.getSettingsNew().getRequestLogSize();
	}

}
