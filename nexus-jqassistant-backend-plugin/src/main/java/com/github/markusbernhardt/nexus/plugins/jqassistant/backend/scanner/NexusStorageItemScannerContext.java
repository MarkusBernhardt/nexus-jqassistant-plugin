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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.nexus.plugins.mavenbridge.internal.DefaultNexusAether;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.maven.maven2.M2Repository;
import org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.MavenArtifactHelper;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.BackendPluginContext;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenArtifactDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenRepositoryDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusRepositoryDescriptor;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusStorageItemDescriptor;

public class NexusStorageItemScannerContext {

	/**
	 * The plugin context
	 */
	protected final BackendPluginContext backendPluginContext;

	/**
	 * The context of the scanned item
	 */
	protected final Map<String, Object> itemContext;

	/**
	 * The list of remote repositories for dependency lookup. Created if needed.
	 */
	protected List<MavenRepository> remoteReposLazy;

	/**
	 * Mapping of Maven artifact to repositories
	 */
	protected Map<String, MavenRepository> artifactToRepositoryMappingLazy;

	/**
	 * Aether workspace reader. Created if needed.
	 */
	protected NexusStorageItemScannerWorkspaceReader workspaceReaderLazy;

	public NexusStorageItemScannerContext(BackendPluginContext backendPluginContext, Map<String, Object> itemContext) {
		super();
		this.backendPluginContext = backendPluginContext;
		this.itemContext = itemContext;
	}

	public void setPrimaryRemoteRepository(MavenRepository repository) {
		List<MavenRepository> remoteRepos = getRemoteRepos();
		remoteRepos.remove(repository);
		remoteRepos.add(0, repository);
	}

	public BackendPluginContext getBackendPluginContext() {
		return backendPluginContext;
	}

	public List<MavenRepository> getRemoteRepos() {
		if (remoteReposLazy == null) {
			remoteReposLazy = new ArrayList<>();
			for (MavenRepository remoteRepo : getBackendPluginContext().getRepositoryRegistry().getRepositoriesWithFacet(MavenRepository.class)) {
				if (!M2Repository.class.isAssignableFrom(remoteRepo.getClass())) {
					continue;
				}
				// TODO Limit repositories via context
				remoteReposLazy.add(remoteRepo);
			}
		}
		return remoteReposLazy;
	}

	public Map<String, MavenRepository> getArtifactToRepositoryMapping() {
		if (artifactToRepositoryMappingLazy == null) {
			artifactToRepositoryMappingLazy = new HashMap<>();
		}
		return artifactToRepositoryMappingLazy;
	}

	public NexusStorageItemScannerWorkspaceReader getWorkspaceReader() {
		if (workspaceReaderLazy == null) {
			workspaceReaderLazy = new NexusStorageItemScannerWorkspaceReader(this);
		}
		return workspaceReaderLazy;
	}

	public Model resolveModel(ModelSource modelSource) throws ModelBuildingException {
		ClassLoader classLoaderBackup = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getBackendPluginContext().getWebappClassLoader());
			if (remoteReposLazy == null || remoteReposLazy.isEmpty()) {
				throw new IllegalArgumentException("Participant repositories in DefaultJqaNexusMavenBridge must have at least one element!");
			}

			MavenRepositorySystemSession session = new MavenRepositorySystemSession();
			LocalRepository localRepo = new LocalRepository(
					getBackendPluginContext().getApplicationDirectories().getWorkDirectory(DefaultNexusAether.LOCAL_REPO_DIR));
			session.setLocalRepositoryManager(getBackendPluginContext().getRepositorySystem().newLocalRepositoryManager(localRepo));
			session.setWorkspaceReader(getWorkspaceReader());
			session.setOffline(true);

			return getBackendPluginContext().getModelResolver()
					.resolveModel(ModelBuildingRequestBuilder.model().setModelSource(modelSource).setProcessPlugins(true), session);
		} finally {
			Thread.currentThread().setContextClassLoader(classLoaderBackup);
		}
	}

	public NexusRepositoryDescriptor getRepositoryDescriptor(Store store, String repositoryId) {
		NexusRepositoryDescriptor repositoryDescriptor = store.find(NexusRepositoryDescriptor.class, repositoryId);
		if (repositoryDescriptor == null) {
			repositoryDescriptor = store.create(NexusRepositoryDescriptor.class);
			repositoryDescriptor.setId(repositoryId);
		}
		return repositoryDescriptor;
	}

	public NexusMavenRepositoryDescriptor getMavenRepositoryDescriptor(Store store, String repositoryId) {
		NexusRepositoryDescriptor repositoryDescriptor = store.find(NexusRepositoryDescriptor.class, repositoryId);
		if (repositoryDescriptor == null) {
			NexusMavenRepositoryDescriptor mavenRepositoryDescriptor = store.create(NexusMavenRepositoryDescriptor.class);
			mavenRepositoryDescriptor.setId(repositoryId);
			return mavenRepositoryDescriptor;
		} else if (NexusMavenRepositoryDescriptor.class.isAssignableFrom(repositoryDescriptor.getClass())) {
			return (NexusMavenRepositoryDescriptor) repositoryDescriptor;
		} else {
			return store.addDescriptorType(repositoryDescriptor, NexusMavenRepositoryDescriptor.class);
		}
	}

	public NexusMavenArtifactDescriptor getMavenArtifactDescriptor(Store store, String repositoryId, Coordinates artifactCoordinates) {
		String storageItemId = Util.getStorageItemUid(repositoryId, artifactCoordinates);
		NexusStorageItemDescriptor storageItemDescriptor = store.find(NexusStorageItemDescriptor.class, storageItemId);
		if (storageItemDescriptor == null) {
			return null;
		} else if (NexusMavenArtifactDescriptor.class.isAssignableFrom(storageItemDescriptor.getClass())) {
			return (NexusMavenArtifactDescriptor) storageItemDescriptor;
		} else {
			return store.addDescriptorType(storageItemDescriptor, NexusMavenArtifactDescriptor.class);
		}
	}

	public NexusMavenArtifactDescriptor getOrCreateArtifactDescriptor(Store store, String repositoryId, Coordinates artifactCoordinates) {
		NexusMavenArtifactDescriptor artifactDescriptor = getMavenArtifactDescriptor(store, repositoryId, artifactCoordinates);
		if (artifactDescriptor == null) {
			artifactDescriptor = store.create(NexusMavenArtifactDescriptor.class, Util.getStorageItemUid(repositoryId, artifactCoordinates));
			artifactDescriptor.setCreatedAt(System.currentTimeMillis());
			artifactDescriptor.setCreatedByAddress(getRequestedByAddress());
			artifactDescriptor.setCreatedByUser(getRequestedByUser());
			artifactDescriptor.setLastUpdatedAt(artifactDescriptor.getCreatedAt());
			artifactDescriptor.setLastUpdatedByAddress(getRequestedByAddress());
			artifactDescriptor.setLastUpdatedByUser(getRequestedByUser());
			MavenArtifactHelper.setCoordinates(artifactDescriptor, artifactCoordinates);
		}
		return artifactDescriptor;
	}

	public String getRequestedByAddress() {
		String address = (String) itemContext.get("request.address");
		if (address == null) {
			address = "";
		}
		return address;
	}

	public String getRequestedByUser() {
		String user = (String) itemContext.get("request.user");
		if (user == null) {
			user = "";
		}
		return user;
	}

}
