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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.uid.IsHiddenAttribute;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.gav.Gav;
import org.sonatype.nexus.proxy.maven.maven2.Maven2ContentClass;
import org.sonatype.nexus.proxy.maven.uid.IsMavenArtifactSignatureAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenChecksumAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenRepositoryMetadataAttribute;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;

public class Util {

	public static boolean checkStorageItemIsMavenArtifact(StorageItem item) {
		Repository repository = item.getRepositoryItemUid().getRepository();
		if (repository.getRepositoryKind().isFacetAvailable(MavenRepository.class)
				&& repository.getRepositoryContentClass().getId().equals(Maven2ContentClass.ID)) {
			return true;
		}
		return false;
	}

	public static boolean checkStorageItemIsRelevantMavenArtifact(StorageItem item) {

		if (item == null || item.isVirtual()) {
			return false;
		}

		if (!StorageFileItem.class.isAssignableFrom(item.getClass()) || ((StorageFileItem) item).isContentGenerated()) {
			return false;
		}

		RepositoryItemUid repositoryItemUid = item.getRepositoryItemUid();
		if (repositoryItemUid.getBooleanAttributeValue(IsHiddenAttribute.class)
				|| repositoryItemUid.getBooleanAttributeValue(IsMavenRepositoryMetadataAttribute.class)
				|| repositoryItemUid.getBooleanAttributeValue(IsMavenArtifactSignatureAttribute.class)
				|| repositoryItemUid.getBooleanAttributeValue(IsMavenChecksumAttribute.class)) {
			return false;
		}

		Repository repository = repositoryItemUid.getRepository();
		if (!repository.getRepositoryKind().isFacetAvailable(MavenRepository.class)
				|| !repository.getRepositoryContentClass().getId().equals(Maven2ContentClass.ID)) {
			return false;
		}

		return true;
	}

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

	public static String getStorageItemUid(String repositoryId, Coordinates coordinates) {
		return getStorageItemUid(repositoryId, toGav(coordinates));
	}

	public static String getStorageItemUid(String repositoryId, Gav gav) {
		StringBuilder path = new StringBuilder(repositoryId);

		path.append(":/");
		path.append(gav.getGroupId().replaceAll("(?m)(.)\\.", "$1/"));
		path.append("/");
		path.append(gav.getArtifactId());
		path.append("/");
		path.append(gav.getBaseVersion());
		path.append("/");
		path.append(calculateArtifactName(gav));

		return path.toString();
	}

	public static String calculateArtifactName(Gav gav) {
		if (gav.getName() != null && gav.getName().trim().length() > 0) {
			return gav.getName();
		} else {
			StringBuilder path = new StringBuilder(gav.getArtifactId());

			path.append("-");
			path.append(gav.getVersion());

			if (gav.getClassifier() != null && gav.getClassifier().trim().length() > 0) {
				path.append("-");
				path.append(gav.getClassifier());
			}

			if (gav.getExtension() != null) {
				path.append(".");
				path.append(gav.getExtension());
			}

			if (gav.isSignature()) {
				path.append(".");
				path.append(gav.getSignatureType().toString());
			}

			if (gav.isHash()) {
				path.append(".");
				path.append(gav.getHashType().toString());
			}

			return path.toString();
		}
	}

	public static File getFileFromStorageFileItem(StorageFileItem storageFileItem) throws LocalStorageException {
		Repository repository = storageFileItem.getRepositoryItemUid().getRepository();
		DefaultFSLocalRepositoryStorage localStorage = (DefaultFSLocalRepositoryStorage) repository.getLocalStorage();
		return localStorage.getFileFromBase(storageFileItem.getRepositoryItemUid().getRepository(), storageFileItem.getResourceStoreRequest());
	}

}
