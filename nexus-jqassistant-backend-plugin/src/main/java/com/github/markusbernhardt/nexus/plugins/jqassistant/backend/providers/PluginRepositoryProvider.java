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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.providers;

import javax.inject.Named;
import javax.inject.Singleton;

import com.buschmais.jqassistant.core.plugin.api.ModelPluginRepository;
import com.buschmais.jqassistant.core.plugin.api.PluginRepository;
import com.buschmais.jqassistant.core.plugin.api.PluginRepositoryException;
import com.buschmais.jqassistant.core.plugin.api.ReportPluginRepository;
import com.buschmais.jqassistant.core.plugin.api.RulePluginRepository;
import com.buschmais.jqassistant.core.plugin.api.ScannerPluginRepository;
import com.buschmais.jqassistant.core.plugin.api.ScopePluginRepository;
import com.buschmais.jqassistant.core.plugin.impl.PluginConfigurationReaderImpl;
import com.buschmais.jqassistant.core.plugin.impl.PluginRepositoryImpl;

@Named
@Singleton
public class PluginRepositoryProvider {

	/**
	 * The plugin repository instance
	 */
	protected final PluginRepository pluginRepository;

	PluginRepositoryProvider() throws PluginRepositoryException {
		this.pluginRepository = new PluginRepositoryImpl(new PluginConfigurationReaderImpl());
	}

	/**
	 * Return the model plugin repository.
	 *
	 * @return The model plugin repository.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *             If the repository cannot be created.
	 */
	public ModelPluginRepository getModelPluginRepository() throws PluginRepositoryException {
		return pluginRepository.getModelPluginRepository();
	}

	/**
	 * Return the scanner plugin repository.
	 *
	 * @return The scanner plugin repository.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *             If the repository cannot be created.
	 */
	public ScannerPluginRepository getScannerPluginRepository() throws PluginRepositoryException {
		return pluginRepository.getScannerPluginRepository();
	}

	/**
	 * Return the scope plugin repository.
	 *
	 * @return The scope plugin repository.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *             If the repository cannot be created.
	 */
	public ScopePluginRepository getScopePluginRepository() throws PluginRepositoryException {
		return pluginRepository.getScopePluginRepository();
	}

	/**
	 * Return the rule plugin repository.
	 *
	 * @return The rule plugin repository.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *             If the repository cannot be created.
	 */
	public RulePluginRepository getRulePluginRepository() throws PluginRepositoryException {
		return pluginRepository.getRulePluginRepository();
	}

	/**
	 * Return the report plugin repository.
	 *
	 * @return The report plugin repository.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *             If the repository cannot be created.
	 */
	public ReportPluginRepository getReportPluginRepository() throws PluginRepositoryException {
		return pluginRepository.getReportPluginRepository();
	}
}
