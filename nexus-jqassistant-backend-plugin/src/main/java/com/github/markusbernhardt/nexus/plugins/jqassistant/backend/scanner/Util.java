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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.uid.IsHiddenAttribute;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.gav.Gav;
import org.sonatype.nexus.proxy.maven.maven2.M2Repository;
import org.sonatype.nexus.proxy.maven.maven2.Maven2ContentClass;
import org.sonatype.nexus.proxy.maven.uid.IsMavenArtifactSignatureAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenChecksumAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenRepositoryMetadataAttribute;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenArtifactDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ArtifactLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.ModelLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.RequestLogXO;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.model.SettingsXO;

public class Util {

	public final static ThreadLocal<SimpleDateFormat> timestampFormatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		}
	};

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
		return new Gav(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), classifier,
				artifact.getExtension(), null, null, null, false, null, false, null);
	}

	public static Gav toGav(Coordinates coordinates) {
		try {
			return new Gav(coordinates.getGroup(), coordinates.getName(), coordinates.getVersion(),
					coordinates.getClassifier(), coordinates.getType(), null, null, null, false, null, false, null);
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
		return localStorage.getFileFromBase(storageFileItem.getRepositoryItemUid().getRepository(),
				storageFileItem.getResourceStoreRequest());
	}

	public static ModelLogXO createModelLogXO(StorageItem item, MavenRepository repository, Model model, File modelFile,
			long duration) {
		ModelLogXO modelLogXO = new ModelLogXO();
		modelLogXO.setUid(item.getRepositoryItemUid().getKey());
		modelLogXO.setRepository(repository.getName());
		modelLogXO.setGroupId(model.getGroupId());
		modelLogXO.setArtifactId(model.getArtifactId());
		modelLogXO.setVersion(model.getVersion());
		modelLogXO.setFilename(modelFile.getAbsolutePath());
		modelLogXO.setDuration(String.format("%,d ms", duration));
		return modelLogXO;
	}

	public static ArtifactLogXO createArtifactLogXO(StorageItem item, MavenRepository repository,
			NexusMavenArtifactDescriptor artifactDescriptor, SettingsXO settingsXO, long duration) {
		ArtifactLogXO artifactLogXO = new ArtifactLogXO();
		artifactLogXO.setUid(item.getRepositoryItemUid().getKey());
		artifactLogXO.setRepository(repository.getName());
		artifactLogXO.setGroupId(artifactDescriptor.getGroup());
		artifactLogXO.setArtifactId(artifactDescriptor.getName());
		artifactLogXO.setVersion(artifactDescriptor.getVersion());
		artifactLogXO.setClassifier(artifactDescriptor.getClassifier());
		artifactLogXO.setExtension(artifactDescriptor.getType());
		artifactLogXO.setFilename(artifactDescriptor.getFileName());
		artifactLogXO.setDuration(String.format("%,d ms", duration));
		artifactLogXO.setFullScan(settingsXO.isFullScan());
		artifactLogXO.setCreatedAt(artifactDescriptor.getCreatedAt() == 0 ? ""
				: timestampFormatter.get().format(new Date(artifactDescriptor.getCreatedAt())));
		artifactLogXO.setCreatedByAddress(artifactDescriptor.getCreatedByAddress());
		artifactLogXO.setCreatedByUser(artifactDescriptor.getCreatedByUser());
		artifactLogXO.setLastUpdatedAt(artifactDescriptor.getLastUpdatedAt() == 0 ? ""
				: timestampFormatter.get().format(new Date(artifactDescriptor.getLastUpdatedAt())));
		artifactLogXO.setLastUpdatedByAddress(artifactDescriptor.getLastUpdatedByAddress());
		artifactLogXO.setLastUpdatedByUser(artifactDescriptor.getLastUpdatedByUser());
		artifactLogXO.setLastRequestedAt(artifactDescriptor.getLastRequestedAt() == 0 ? ""
				: timestampFormatter.get().format(new Date(artifactDescriptor.getLastRequestedAt())));
		artifactLogXO.setLastRequestedByAddress(artifactDescriptor.getLastRequestedByAddress());
		artifactLogXO.setLastRequestedByUser(artifactDescriptor.getLastRequestedByUser());
		artifactLogXO.setRequestCount(String.format("%,d", artifactDescriptor.getRequestCount()));
		artifactLogXO.setDescriptors(formatDescriptors(artifactDescriptor.toString()));
		return artifactLogXO;
	}

	public static RequestLogXO createRequestLogXO(StorageItem item, MavenRepository repository,
			NexusMavenArtifactDescriptor artifactDescriptor) {
		RequestLogXO requestLogXO = new RequestLogXO();
		requestLogXO.setUid(item.getRepositoryItemUid().getKey());
		requestLogXO.setRepository(repository.getName());
		requestLogXO.setGroupId(artifactDescriptor.getGroup());
		requestLogXO.setArtifactId(artifactDescriptor.getName());
		requestLogXO.setVersion(artifactDescriptor.getVersion());
		requestLogXO.setClassifier(artifactDescriptor.getClassifier());
		requestLogXO.setExtension(artifactDescriptor.getType());
		requestLogXO.setFilename(artifactDescriptor.getFileName());
		requestLogXO.setRequestedAt(artifactDescriptor.getLastRequestedAt() == 0 ? ""
				: timestampFormatter.get().format(new Date(artifactDescriptor.getLastRequestedAt())));
		requestLogXO.setRequestedByAddress(artifactDescriptor.getLastRequestedByAddress());
		requestLogXO.setRequestedByUser(artifactDescriptor.getLastRequestedByUser());
		requestLogXO.setRequestCount(String.format("%,d", artifactDescriptor.getRequestCount()));
		return requestLogXO;
	}

	public static String formatDescriptors(String descriptorString) {
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

	public static List<M2Repository> getM2RepositoryList(RepositoryRegistry repositoryRegistry) {
		List<M2Repository> m2RepositoryList = new ArrayList<>();
		for (MavenRepository m2Repository : repositoryRegistry.getRepositoriesWithFacet(MavenRepository.class)) {
			if (!M2Repository.class.isAssignableFrom(m2Repository.getClass())) {
				continue;
			}
			m2RepositoryList.add((M2Repository) m2Repository);
		}
		return m2RepositoryList;
	}
}
