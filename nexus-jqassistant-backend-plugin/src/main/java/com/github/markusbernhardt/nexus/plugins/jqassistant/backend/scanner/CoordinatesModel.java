package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner;

import org.sonatype.nexus.proxy.maven.gav.Gav;

import com.buschmais.jqassistant.plugin.maven3.api.artifact.Coordinates;

public class CoordinatesModel implements Coordinates {

	protected final String group;

	protected final String name;

	protected final String version;

	public CoordinatesModel(Coordinates coordinates) {
		super();
		this.group = coordinates.getGroup();
		this.name = coordinates.getName();
		this.version = coordinates.getVersion();
	}

	public CoordinatesModel(Gav gav) {
		super();
		this.group = gav.getGroupId();
		this.name = gav.getArtifactId();
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
		return null;
	}

	@Override
	public String getType() {
		return "pom";
	}

	@Override
	public String getVersion() {
		return version;
	}

}
