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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.maven.gav.Gav;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;

public class Util {

	public static Gav toGav(Artifact artifact) {
		// fix for bug in M2GavCalculator
		String classifier = StringUtils.isEmpty(artifact.getClassifier()) ? null : artifact.getClassifier();
		return new Gav(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), classifier, artifact.getExtension(), null, null, null, false,
				null, false, null);
	}

	public static Gav toGav(Coordinates coordinates) {
		try {
			return new Gav(coordinates.getGroup(), coordinates.getName(), coordinates.getVersion(), coordinates.getClassifier(), coordinates.getType(), null,
					null, null, false, null, false, null);
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public static File getFileFromStorageFileItem(StorageFileItem storageFileItem) throws LocalStorageException {
		Repository repository = storageFileItem.getRepositoryItemUid().getRepository();
		DefaultFSLocalRepositoryStorage localStorage = (DefaultFSLocalRepositoryStorage) repository.getLocalStorage();
		return localStorage.getFileFromBase(storageFileItem.getRepositoryItemUid().getRepository(), storageFileItem.getResourceStoreRequest());
	}

}
