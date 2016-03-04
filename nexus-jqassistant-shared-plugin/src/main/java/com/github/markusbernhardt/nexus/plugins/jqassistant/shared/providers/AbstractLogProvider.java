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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.sonatype.nexus.events.EventSubscriber;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.google.common.eventbus.Subscribe;

public abstract class AbstractLogProvider<T, L> implements EventSubscriber {

	/**
	 * The central list of scans
	 */
	protected final LinkedList<T> log;

	/**
	 * The sequence counter
	 */
	protected final AtomicInteger sequence;

	/**
	 * The max log size
	 */
	protected int maxLogSize;

	/**
	 * The date formatter
	 */
	protected final static ThreadLocal<SimpleDateFormat> timestampFormatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		}
	};

	public AbstractLogProvider(int maxLogSize) {
		this.maxLogSize = maxLogSize;
		this.log = new LinkedList<>();
		this.sequence = new AtomicInteger(0);
	}

	@Subscribe
	public synchronized void onSettingsChange(SettingsEvent evt) {
		int newMaxLogSize = getNewMaxLogSize(evt);
		if (maxLogSize != newMaxLogSize) {
			maxLogSize = newMaxLogSize;
			trimSize();
		}
	}

	public synchronized L getLogList(int start, int limit) {
		List<T> rows = new ArrayList<>(limit);
		if (start >= 0 && start < log.size()) {
			int end = start + limit - 1;
			int i = 0;
			for (T request : log) {
				if (start <= i) {
					rows.add(request);
				}
				if (end == i) {
					break;
				}
				i++;
			}
		}
		return createLogList(rows.size(), log.size(), rows);
	}

	public synchronized void clear() {
		log.clear();
	}

	public synchronized void trimSize() {
		while (log.size() > maxLogSize) {
			log.removeFirst();
		}
	}

	protected String formatDescriptors(String descriptorString) {
		StringBuilder formattedDescriptors = new StringBuilder();
		String verkett = "";
		String[] tokens = descriptorString.split(",|\\|");
		Arrays.sort(tokens);
		for (String token : tokens) {
			if (!token.endsWith("Descriptor")) {
				continue;
			}
			token.substring(0, token.length() - 10);
			if (token.length() == 0) {
				continue;
			}
			formattedDescriptors.append(verkett);
			formattedDescriptors.append(token);
			verkett = "<br>";
		}
		return formattedDescriptors.toString();
	}

	protected abstract L createLogList(int count, int total, List<T> rows);

	protected abstract int getNewMaxLogSize(SettingsEvent evt);
}