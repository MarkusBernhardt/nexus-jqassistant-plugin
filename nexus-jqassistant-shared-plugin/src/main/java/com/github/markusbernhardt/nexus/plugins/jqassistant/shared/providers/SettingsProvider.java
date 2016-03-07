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

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.SharedPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.SettingsEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.SettingsXO;

@Named
@Singleton
public class SettingsProvider {

	public static final String SETTINGS_FILENAME = "jqassistant.xml";

	/**
	 * The plug in context
	 */
	protected final SharedPluginContext pluginContext;

	/**
	 * The file to read from or write to
	 */
	protected final File settingsFile;

	/**
	 * The central SettingsXO instance
	 */
	protected SettingsXO settings;

	@Inject
	SettingsProvider(SharedPluginContext pluginContext) {
		this.pluginContext = pluginContext;
		this.settingsFile = new File(pluginContext.getApplicationConfiguration().getConfigurationDirectory(), SETTINGS_FILENAME);
		this.settings = loadOrCreateSettingsXO();
	}

	public SettingsXO getSettings() {
		return settings;
	}

	public void setSettings(SettingsXO settingsNew) {
		try {
			SettingsXO settingsOld = settings;
			settings = settingsNew;

			if (!settingsOld.isActivated()) {
				// Preserve values of disabled input fields
				settingsNew.setFullScan(settingsOld.isFullScan());
				settingsNew.setModelLogSize(settingsOld.getModelLogSize());
				settingsNew.setArtifactLogSize(settingsOld.getArtifactLogSize());
				settingsNew.setRequestLogSize(settingsOld.getRequestLogSize());
			}
			
			if (settingsNew.getCommandQueueSize() < 100) {
				settingsNew.setCommandQueueSize(100);
			}

			JAXBContext jaxbContext = JAXBContext.newInstance(SettingsXO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(settingsNew, settingsFile);

			pluginContext.getEventBus().post(new SettingsEvent(this, settingsNew, settingsOld));
		} catch (JAXBException e) {
			pluginContext.getLogger().error(e.getMessage(), e);
		}
	}

	protected SettingsXO loadOrCreateSettingsXO() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SettingsXO.class);
			if (settingsFile.exists()) {
				// Settings file does exist => read
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				SettingsXO settings = (SettingsXO) jaxbUnmarshaller.unmarshal(settingsFile);
				if (settings.getCommandQueueSize() < 100) {
					settings.setCommandQueueSize(100);
				}
				return settings;
			} else {
				// Settings file does not exist => create
				SettingsXO settings = new SettingsXO();
				settings.setActivated(false);
				settings.setFullScan(false);
				settings.setCommandQueueSize(10000);
				settings.setModelLogSize(10000);
				settings.setArtifactLogSize(10000);
				settings.setRequestLogSize(10000);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.marshal(settings, settingsFile);
				return settings;
			}
		} catch (JAXBException e) {
			pluginContext.getLogger().error(e.getMessage(), e);
		}
		return null;
	}

}
