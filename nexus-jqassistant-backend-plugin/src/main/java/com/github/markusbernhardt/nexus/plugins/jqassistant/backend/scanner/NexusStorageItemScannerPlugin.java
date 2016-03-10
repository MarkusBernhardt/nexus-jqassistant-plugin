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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.slf4j.Logger;
import org.sonatype.nexus.plugins.mavenbridge.internal.FileItemModelSource;
import org.sonatype.nexus.proxy.AccessDeniedException;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.maven.ArtifactStoreRequest;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.gav.Gav;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.model.ArtifactDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.ArtifactResolver;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.MavenArtifactHelper;
import com.buschmais.jqassistant.plugin.maven3.api.model.MavenDescriptor;
import com.buschmais.jqassistant.plugin.maven3.api.model.MavenPomXmlDescriptor;
import com.buschmais.jqassistant.plugin.maven3.api.scanner.PomModelBuilder;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.MavenReleaseDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.MavenSnapshotDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenArtifactDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenRepositoryDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusStorageItemDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ArtifactLogEvent;
import com.github.markusbernhardt.nexus.plugins.jqassistant.shared.events.ModelLogEvent;

public class NexusStorageItemScannerPlugin extends AbstractScannerPlugin<StorageItem, NexusStorageItemDescriptor> {

	// TODO Limit which items to scan

	@Override
	public boolean accepts(StorageItem item, String path, Scope scope) throws IOException {
		if (scope == null || !NexusScope.class.isAssignableFrom(scope.getClass())) {
			return false;
		}
		return true;
	}

	@Override
	public NexusMavenArtifactDescriptor scan(StorageItem item, String path, Scope scope, Scanner scanner) throws IOException {
		if (Util.checkStorageItemIsMavenArtifact(item)) {
			if (NexusScope.SCAN.equals(scope)) {
				return scanMaven(item, path, scanner);
			} else if (NexusScope.LOG.equals(scope)) {
				return logMaven(item, path, scanner);
			}
		}
		return null;
	}

	public NexusMavenArtifactDescriptor logMaven(StorageItem item, String path, Scanner scanner) throws IOException {
		return null;
	}

	protected NexusMavenArtifactDescriptor scanMaven(StorageItem item, String path, Scanner scanner) throws IOException {
		if (!Util.checkStorageItemIsRelevantMavenArtifact(item)) {
			return null;
		}

		ScannerContext context = scanner.getContext();
		Store store = context.getStore();
		NexusStorageItemScannerContext nexusStorageItemScannerContext = context.peek(NexusStorageItemScannerContext.class);
		BackendPluginContext backendPluginContext = nexusStorageItemScannerContext.getBackendPluginContext();
		Logger logger = backendPluginContext.getLogger();

		MavenRepository repository = item.getRepositoryItemUid().getRepository().adaptToFacet(MavenRepository.class);
		nexusStorageItemScannerContext.setPrimaryRemoteRepository(repository);

		Gav gav = repository.getGavCalculator().pathToGav(item.getRepositoryItemUid().getPath());
		if (gav == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(String.format("Could not determine GAV for StorageItem '%s'.", item.getRepositoryItemUid().getKey()));
			}
			return null;
		}
		boolean isPom = gav.getExtension().equals("pom");

		String repositoryId = repository.getId();
		NexusMavenRepositoryDescriptor repositoryDescriptor = nexusStorageItemScannerContext.getMavenRepositoryDescriptor(store, repositoryId);

		String modelId = MavenArtifactHelper.getId(new CoordinatesModel(gav));
		nexusStorageItemScannerContext.getArtifactToRepositoryMapping().put(modelId, repository);

		Set<ArtifactDescriptor> describesBackup = new HashSet<>();
		MavenPomXmlDescriptor modelDescriptor = repositoryDescriptor.findModel(modelId);
		if (modelDescriptor != null && isPom) {
			describesBackup.addAll(modelDescriptor.getDescribes());
			repositoryDescriptor.detachDeleteModel(modelId);
			modelDescriptor = null;
		}

		CoordinatesArtifact artifactCoordinates = new CoordinatesArtifact(gav);
		NexusMavenArtifactDescriptor artifactDescriptorBackup = nexusStorageItemScannerContext.getMavenArtifactDescriptor(store, repositoryId,
				artifactCoordinates);

		if (modelDescriptor == null) {
			try {
				// Scan Maven model
				StorageFileItem modelItem = (StorageFileItem) item;
				if (!isPom) {
					// item is no Maven model => locate model item
					Gav modelGav = new Gav(gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), null, "pom", gav.getSnapshotBuildNumber(),
							gav.getSnapshotTimeStamp(), null, false, null, false, null);

					ArtifactStoreRequest modelRequest = new ArtifactStoreRequest(repository, modelGav, false);
					modelRequest.getRequestContext().put(AccessManager.REQUEST_AUTHORIZED, Boolean.TRUE);

					modelItem = (StorageFileItem) repository.retrieveItem(modelRequest);
				}
				File modelFile = Util.getFileFromStorageFileItem(modelItem);

				final Model model = nexusStorageItemScannerContext.resolveModel(new FileItemModelSource(modelItem));
				context.push(PomModelBuilder.class, new PomModelBuilder() {
					@Override
					public Model getModel(File pomFile) throws IOException {
						return model;
					}
				});

				context.push(ArtifactResolver.class, new NexusStorageItemScannerArtifactResolver(nexusStorageItemScannerContext));
				try {
					long start = System.currentTimeMillis();
					modelDescriptor = scanner.scan(modelFile, modelFile.getAbsolutePath(), null);
					long duration = System.currentTimeMillis() - start;

					backendPluginContext.getEventBus().post(new ModelLogEvent(this, item.getRepositoryItemUid().getKey(), repository.getName(),
							model.getGroupId(), model.getArtifactId(), model.getVersion(), modelFile.getAbsolutePath(), duration));
				} finally {
					context.pop(ArtifactResolver.class);
					context.pop(PomModelBuilder.class);
				}

				modelDescriptor = markReleaseOrSnaphot(modelDescriptor, MavenPomXmlDescriptor.class, gav, store);
				modelDescriptor.getDescribes().addAll(describesBackup);
				repositoryDescriptor.getContainedModels().add(modelDescriptor);
			} catch (AccessDeniedException | ItemNotFoundException | IllegalOperationException | ModelBuildingException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}
		}

		long start = System.currentTimeMillis();
		StorageFileItem artifactItem = (StorageFileItem) item;
		File artifactFile = Util.getFileFromStorageFileItem(artifactItem);

		NexusMavenArtifactDescriptor artifactDescriptor = null;
		if (isPom) {
			artifactDescriptor = nexusStorageItemScannerContext.getMavenArtifactDescriptor(store, repositoryId, artifactCoordinates);
			if (artifactDescriptorBackup == null) {
				artifactDescriptor.setFileName(artifactFile.getAbsolutePath());
				artifactDescriptor = markReleaseOrSnaphot(artifactDescriptor, NexusMavenArtifactDescriptor.class, gav, store);
				repositoryDescriptor.getContainedItems().add(artifactDescriptor);
			} else {
				artifactDescriptor.setLastUpdatedAt(System.currentTimeMillis());
				artifactDescriptor.setLastUpdatedByAddress(nexusStorageItemScannerContext.getRequestedByAddress());
				artifactDescriptor.setLastUpdatedByUser(nexusStorageItemScannerContext.getRequestedByUser());
			}
		} else {
			if (backendPluginContext.getSettingsProvider().getSettings().isFullScan()) {
				// Full scan
				Descriptor descriptor = scanner.scan(artifactFile, artifactFile.getAbsolutePath(), null);
				artifactDescriptor = store.addDescriptorType(descriptor, NexusMavenArtifactDescriptor.class);
				artifactDescriptor.setFullQualifiedName(item.getRepositoryItemUid().getKey());
			} else {
				// Regular scan
				artifactDescriptor = store.create(NexusMavenArtifactDescriptor.class, item.getRepositoryItemUid().getKey());
			}

			MavenArtifactHelper.setCoordinates(artifactDescriptor, artifactCoordinates);
			artifactDescriptor.setLastUpdatedByAddress(nexusStorageItemScannerContext.getRequestedByAddress());
			artifactDescriptor.setLastUpdatedByUser(nexusStorageItemScannerContext.getRequestedByUser());
			artifactDescriptor.setFileName(artifactFile.getAbsolutePath());
			if (artifactDescriptorBackup == null) {
				artifactDescriptor.setCreatedAt(System.currentTimeMillis());
				artifactDescriptor.setCreatedByAddress(nexusStorageItemScannerContext.getRequestedByAddress());
				artifactDescriptor.setCreatedByUser(nexusStorageItemScannerContext.getRequestedByUser());
				artifactDescriptor.setLastUpdatedAt(artifactDescriptor.getCreatedAt());
			} else {
				artifactDescriptor.setCreatedAt(artifactDescriptorBackup.getCreatedAt());
				artifactDescriptor.setCreatedByAddress(artifactDescriptorBackup.getCreatedByAddress());
				artifactDescriptor.setCreatedByUser(artifactDescriptorBackup.getCreatedByUser());
				artifactDescriptor.setLastUpdatedAt(System.currentTimeMillis());
				artifactDescriptor.setLastRequestedAt(artifactDescriptorBackup.getLastRequestedAt());
				artifactDescriptor.setLastRequestedByAddress(artifactDescriptorBackup.getLastRequestedByAddress());
				artifactDescriptor.setLastRequestedByUser(artifactDescriptorBackup.getLastRequestedByUser());
				artifactDescriptor.setRequestCount(artifactDescriptorBackup.getRequestCount());

				artifactDescriptorBackup.detachDelete();
			}

			artifactDescriptor = markReleaseOrSnaphot(artifactDescriptor, NexusMavenArtifactDescriptor.class, gav, store);
			modelDescriptor.getDescribes().add(artifactDescriptor);
			repositoryDescriptor.getContainedItems().add(artifactDescriptor);
		}

		long duration = System.currentTimeMillis() - start;
		backendPluginContext.getEventBus()
				.post(new ArtifactLogEvent(this, item.getRepositoryItemUid().getKey(), repository.getName(), artifactDescriptor.getGroup(),
						artifactDescriptor.getName(), artifactDescriptor.getVersion(), artifactDescriptor.getClassifier(), artifactDescriptor.getType(),
						artifactFile.getAbsolutePath(), duration, backendPluginContext.getSettingsProvider().getSettings().isFullScan(),
						artifactDescriptor.getCreatedAt(), artifactDescriptor.getCreatedByAddress(), artifactDescriptor.getCreatedByUser(),
						artifactDescriptor.getLastUpdatedAt(), artifactDescriptor.getLastUpdatedByAddress(), artifactDescriptor.getLastUpdatedByUser(),
						artifactDescriptor.getLastRequestedAt(), artifactDescriptor.getLastRequestedByAddress(), artifactDescriptor.getLastRequestedByUser(),
						artifactDescriptor.getRequestCount(), artifactDescriptor.toString()));

		return artifactDescriptor;
	}

	protected <D extends MavenDescriptor> D markReleaseOrSnaphot(D descriptor, Class<? extends D> type, Gav gav, Store store) {
		if (gav.isSnapshot()) {
			MavenSnapshotDescriptor snapshotDescriptor = store.addDescriptorType(descriptor, MavenSnapshotDescriptor.class);
			snapshotDescriptor.setTimestamp(gav.getSnapshotTimeStamp());
			snapshotDescriptor.setSnapshotBuildNumber(gav.getSnapshotBuildNumber());
			return type.cast(snapshotDescriptor);
		} else {
			return store.addDescriptorType(descriptor, MavenReleaseDescriptor.class, type);
		}
	}

}
