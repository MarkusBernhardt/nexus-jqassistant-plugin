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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.nexus.proxy.AccessDeniedException;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.maven.ArtifactStoreRequest;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.gav.Gav;
import org.sonatype.nexus.proxy.maven.maven2.M2Repository;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.MavenArtifactHelper;

/**
 * Aether workspace reader
 */
public class NexusStorageItemScannerWorkspaceReader implements WorkspaceReader {

	protected final NexusStorageItemScannerContext nexusStorageItemScannerContext;

	protected final WorkspaceRepository workspaceRepository;

	public NexusStorageItemScannerWorkspaceReader(NexusStorageItemScannerContext nexusStorageItemScannerContext) {
		this.nexusStorageItemScannerContext = nexusStorageItemScannerContext;
		this.workspaceRepository = new WorkspaceRepository("nexus", UUID.randomUUID().toString());
	}

	@Override
	public WorkspaceRepository getRepository() {
		return workspaceRepository;
	}

	/**
	 * This method will in case of released artifact request just locate it, and
	 * return if found. In case of snapshot repository, if it needs resolving,
	 * will resolve it 1st and than locate it. It will obey to the session
	 * (global update policy, that correspondos to Maven CLI "-U" option.
	 */
	@Override
	public File findArtifact(Artifact artifact) {
		return findArtifact(Util.toGav(artifact));
	}

	public File findArtifact(Gav gav) {
		StorageFileItem storageFileItem = findStorageItem(gav);
		if (storageFileItem != null) {
			try {
				return Util.getFileFromStorageFileItem(storageFileItem);
			} catch (LocalStorageException e) {
			}
		}
		return null;
	}

	public StorageFileItem findStorageItem(Gav gav) {
		for (M2Repository repository : nexusStorageItemScannerContext.getRemoteRepos()) {
			try {
				ArtifactStoreRequest gavRequest = new ArtifactStoreRequest(repository, gav, false);
				gavRequest.getRequestContext().put(AccessManager.REQUEST_AUTHORIZED, Boolean.TRUE);

				StorageFileItem storageFileItem = repository.getArtifactStoreHelper().retrieveArtifact(gavRequest);

				// Save the repository mapping
				nexusStorageItemScannerContext.getArtifactToRepositoryMapping().put(MavenArtifactHelper.getId(new CoordinatesModel(gav)), repository);

				return storageFileItem;
			} catch (IOException | AccessDeniedException | IllegalOperationException | ItemNotFoundException e) {
			}
		}
		return null;
	}

	/**
	 * Basically, this method will read the GA metadata, and return the
	 * "known versions".
	 */
	@Override
	public List<String> findVersions(Artifact artifact) {
		Gav gav = Util.toGav(artifact);

		if (gav.isSnapshot()) {
			ArtifactStoreRequest gavRequest;

			for (MavenRepository repository : nexusStorageItemScannerContext.getRemoteRepos()) {
				gavRequest = new ArtifactStoreRequest(repository, gav, false, false);
				// Not sure, why this is neccessary!
				gavRequest.getRequestContext().put(AccessManager.REQUEST_AUTHORIZED, Boolean.TRUE);

				try {
					Gav snapshot = repository.getMetadataManager().resolveSnapshot(gavRequest, gav);
					return Collections.singletonList(snapshot.getVersion());
				} catch (Exception e) {
					// try next repo
					continue;
				}
			}
		}

		return Collections.emptyList();
	}

}
