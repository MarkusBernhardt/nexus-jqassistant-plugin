package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner;

import org.sonatype.nexus.proxy.maven.gav.Gav;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;

public class CoordinatesArtifact implements Coordinates {

	protected final String group;

	protected final String name;

	protected final String version;

	protected final String classifier;

	protected final String type;

	public CoordinatesArtifact(Coordinates coordinates) {
		super();
		this.group = coordinates.getGroup();
		this.name = coordinates.getName();
		this.classifier = coordinates.getClassifier();
		this.type = coordinates.getType();
		this.version = coordinates.getVersion();
	}

	public CoordinatesArtifact(Gav gav) {
		super();
		this.group = gav.getGroupId();
		this.name = gav.getArtifactId();
		this.classifier = gav.getClassifier();
		this.type = gav.getExtension();
		this.version = gav.getVersion();
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassifier() {
		return classifier;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
