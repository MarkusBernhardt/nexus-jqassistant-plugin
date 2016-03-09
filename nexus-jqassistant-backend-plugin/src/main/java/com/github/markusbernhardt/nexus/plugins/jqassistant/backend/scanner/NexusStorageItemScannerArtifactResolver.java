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

import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.maven.MavenRepository;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.ArtifactResolver;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;
import com.buschmais.jqassistant.plugin.maven3.api.artifact.MavenArtifactHelper;
import com.buschmais.jqassistant.plugin.maven3.impl.scanner.artifact.ModelCoordinates;
import com.buschmais.jqassistant.plugin.maven3.impl.scanner.artifact.ParentCoordinates;
import com.buschmais.jqassistant.plugin.maven3.impl.scanner.artifact.PluginCoordinates;
import com.github.markusbernhardt.nexus.plugins.jqassistant.backend.descriptors.NexusMavenArtifactDescriptor;

public class NexusStorageItemScannerArtifactResolver implements ArtifactResolver {

	protected final NexusStorageItemScannerContext nexusStorageItemScannerContext;

	public NexusStorageItemScannerArtifactResolver(NexusStorageItemScannerContext nexusStorageItemScannerContext) {
		this.nexusStorageItemScannerContext = nexusStorageItemScannerContext;
	}

	@Override
	public NexusMavenArtifactDescriptor resolve(Coordinates artifactCoordinates, ScannerContext scannerContext) {
		String repositoryId = null;
		Coordinates modelCoordinates = new CoordinatesModel(artifactCoordinates);
		if (ModelCoordinates.class.isAssignableFrom(artifactCoordinates.getClass())
				|| ParentCoordinates.class.isAssignableFrom(artifactCoordinates.getClass())) {
			artifactCoordinates = modelCoordinates;
		} else if (PluginCoordinates.class.isAssignableFrom(artifactCoordinates.getClass())) {
			repositoryId = "plugins";
			artifactCoordinates = new CoordinatesUnknown(artifactCoordinates);
		}

		if (repositoryId == null) {
			String modelId = MavenArtifactHelper.getId(modelCoordinates);
			MavenRepository mavenRepository = nexusStorageItemScannerContext.getArtifactToRepositoryMapping()
					.get(modelId);
			if (mavenRepository != null) {
				repositoryId = mavenRepository.getId();
			}
		}

		if (repositoryId == null) {
			StorageFileItem modelItem = nexusStorageItemScannerContext.getWorkspaceReader()
					.findStorageItem(Util.toGav(modelCoordinates));
			if (modelItem != null) {
				MavenRepository mavenRepository = (MavenRepository) modelItem.getRepositoryItemUid().getRepository();
				if (mavenRepository != null) {
					repositoryId = mavenRepository.getId();
				}
			}
		}

		if (repositoryId == null) {
			repositoryId = "provided";
		}

		return nexusStorageItemScannerContext.getOrCreateArtifactDescriptor(scannerContext.getStore(), repositoryId,
				artifactCoordinates);
	}

}
