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

/*global NX, Ext, Sonatype, Nexus*/
/**
 * JQAssistant administration panel controller.
 */
NX.define('Nexus.jqassistant.controller.AdministrationPanelController', {
	extend : 'Nexus.controller.Controller',

	requires : [ 'Nexus.siesta', 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.view.AdministrationPanel' ],

	init : function() {
		var me = this;

		me.control({
			'#nx-jqassistant-view-settings' : {
				activate : me.loadSettings
			},
			'#nx-jqassistant-view-settings-button-save' : {
				click : me.saveSettings
			},
			'#nx-jqassistant-view-model-log' : {
				activate : me.loadModelLog
			},
			'#nx-jqassistant-view-model-log-button-refresh' : {
				click : me.refreshModelLog
			},
			'#nx-jqassistant-view-model-log-button-clear' : {
				click : me.clearModelLog
			},
			'#nx-jqassistant-view-artifact-log' : {
				activate : me.loadArtifactLog
			},
			'#nx-jqassistant-view-artifact-log-button-refresh' : {
				click : me.refreshArtifactLog
			},
			'#nx-jqassistant-view-artifact-log-button-clear' : {
				click : me.clearArtifactLog
			},
			'#nx-jqassistant-view-request-log' : {
				activate : me.loadRequestLog
			},
			'#nx-jqassistant-view-request-log-button-refresh' : {
				click : me.refreshRequestLog
			},
			'#nx-jqassistant-view-request-log-button-clear' : {
				click : me.clearRequestLog
			}
		});

		me.addNavigationMenu();

	},

	/**
	 * @private
	 */
	addNavigationMenu : function() {
		Sonatype.Events.on('nexusNavigationInit', function(panel) {
			var sp = Sonatype.lib.Permissions;

			panel.add({
				enabled : sp.checkPermission('nexus:jqassistant', sp.READ),
				sectionId : 'st-nexus-config',
				title : 'jQAssistant',
				tabId : 'jqassistant',
				tabCode : function() {
					return Ext.create({
						xtype : 'nx-jqassistant-view-administration-panel',
						id : 'nx-jqassistant-view-administration-panel'
					});
				}
			});
		});
	},

	/**
	 * @private
	 */
	showMessage : function(message) {
		Nexus.messages.show('jQAssistant', message);
	},

	/**
	 * @private
	 */
	loadSettings : function() {
		var me = this;

		me.logDebug('Loading settings');

		Ext.Ajax.request({
			url : Nexus.siesta.basePath + '/jqassistant/settings',
			method : 'GET',

			scope : me,
			success : function(response, opts) {
				me.logDebug('Settings: ' + response.responseText);
				var values = Ext.decode(response.responseText);

				Ext.getCmp('nx-jqassistant-view-settings').setValues(values);

				var tabPanel = Ext.getCmp('nx-jqassistant-view-administration-panel').down('tabpanel');
				var modelLogView = Ext.getCmp('nx-jqassistant-view-model-log');
				var artifactLogView = Ext.getCmp('nx-jqassistant-view-artifact-log');
				var requestLogView = Ext.getCmp('nx-jqassistant-view-request-log');

				var settingsFullScanField = Ext.getCmp('nx-jqassistant-view-settings-form-full-scan');
				var settingsCommandQueueSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-command-queue-size');
				var settingsModelLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-model-log-size');
				var settingsArtifactLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-artifact-log-size');
				var settingsRequestLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-request-log-size');
				if (values.activated === true) {
					tabPanel.unhideTabStripItem(modelLogView);
					tabPanel.unhideTabStripItem(artifactLogView);
					tabPanel.unhideTabStripItem(requestLogView);

					settingsFullScanField.setDisabled(false);
					settingsCommandQueueSizeField.setDisabled(false);
					settingsModelLogSizeField.setDisabled(false);
					settingsArtifactLogSizeField.setDisabled(false);
					settingsRequestLogSizeField.setDisabled(false);
				} else {
					tabPanel.hideTabStripItem(modelLogView);
					tabPanel.hideTabStripItem(artifactLogView);
					tabPanel.hideTabStripItem(requestLogView);

					settingsFullScanField.setDisabled(true);
					settingsCommandQueueSizeField.setDisabled(true);
					settingsModelLogSizeField.setDisabled(true);
					settingsArtifactLogSizeField.setDisabled(true);
					settingsRequestLogSizeField.setDisabled(true);
				}
			}
		});
	},

	/**
	 * @private
	 */
	saveSettings : function(button) {
		var me = this, values = Ext.getCmp('nx-jqassistant-view-settings').getValues();

		me.logDebug('Saving settings: ' + Ext.util.JSON.encode(values));

		Ext.Ajax.request({
			url : Nexus.siesta.basePath + '/jqassistant/settings',
			method : 'PUT',
			jsonData : values,

			scope : me,
			success : function() {
				me.showMessage('Settings saved');

				// reload settings to apply view customiztions
				me.loadSettings();
			}
		});
	},

	/**
	 * @private
	 */
	loadModelLog : function(panel) {
		var store = panel.getGrid().getStore();
		store.load({
			params : {
				start : 0,
				limit : Nexus.jqassistant.store.ModelLog.PAGE_SIZE
			}
		});
	},

	/**
	 * @private
	 */
	refreshModelLog : function(button) {
		var me = this, panel = button.up('nx-jqassistant-view-model-log');
		me.loadModelLog(panel);
	},

	/**
	 * @private
	 */
	clearModelLog : function(button) {
		var me = this, icons = Nexus.jqassistant.Icons, store = button.up('nx-jqassistant-view-model-log').getGrid().getStore();

		Ext.Msg.show({
			title : 'Clear log',
			msg : 'Clear the model log?',
			buttons : Ext.Msg.OKCANCEL,
			icon : icons.get('clear').variant('x16').cls,
			fn : function(btn) {
				if (btn === 'ok') {
					Ext.Ajax.request({
						url : Nexus.siesta.basePath + '/jqassistant/model-log',
						method : 'DELETE',
						suppressStatus : true,
						success : function() {
							me.showMessage('The model log has been cleared');
							store.load({
								params : {
									start : 0,
									limit : Nexus.jqassistant.store.ModelLog.PAGE_SIZE
								}
							});
						},
						failure : function(response) {
							me.showMessage('Failed to clear the model log: ' + me.parseExceptionMessage(response));
						}
					});
				}
			}
		});
	},

	/**
	 * @private
	 */
	loadArtifactLog : function(panel) {
		var store = panel.getGrid().getStore();
		store.load({
			params : {
				start : 0,
				limit : Nexus.jqassistant.store.ArtifactLog.PAGE_SIZE
			}
		});
	},

	/**
	 * @private
	 */
	refreshArtifactLog : function(button) {
		var me = this, panel = button.up('nx-jqassistant-view-artifact-log');
		me.loadArtifactLog(panel);
	},

	/**
	 * @private
	 */
	clearArtifactLog : function(button) {
		var me = this, icons = Nexus.jqassistant.Icons, store = button.up('nx-jqassistant-view-artifact-log').getGrid().getStore();

		Ext.Msg.show({
			title : 'Clear log',
			msg : 'Clear the artifact log?',
			buttons : Ext.Msg.OKCANCEL,
			icon : icons.get('clear').variant('x16').cls,
			fn : function(btn) {
				if (btn === 'ok') {
					Ext.Ajax.request({
						url : Nexus.siesta.basePath + '/jqassistant/artifact-log',
						method : 'DELETE',
						suppressStatus : true,
						success : function() {
							me.showMessage('The artifact log has been cleared');
							store.load({
								params : {
									start : 0,
									limit : Nexus.jqassistant.store.ArtifactLog.PAGE_SIZE
								}
							});
						},
						failure : function(response) {
							me.showMessage('Failed to clear the artifact log: ' + me.parseExceptionMessage(response));
						}
					});
				}
			}
		});
	},

	/**
	 * @private
	 */
	loadRequestLog : function(panel) {
		var store = panel.getGrid().getStore();
		store.load({
			params : {
				start : 0,
				limit : Nexus.jqassistant.store.RequestLog.PAGE_SIZE
			}
		});
	},

	/**
	 * @private
	 */
	refreshRequestLog : function(button) {
		var me = this, panel = button.up('nx-jqassistant-view-request-log');
		me.loadRequestLog(panel);
	},

	/**
	 * @private
	 */
	clearRequestLog : function(button) {
		var me = this, icons = Nexus.jqassistant.Icons, store = button.up('nx-jqassistant-view-request-log').getGrid().getStore();

		Ext.Msg.show({
			title : 'Clear log',
			msg : 'Clear the request log?',
			buttons : Ext.Msg.OKCANCEL,
			icon : icons.get('clear').variant('x16').cls,
			fn : function(btn) {
				if (btn === 'ok') {
					Ext.Ajax.request({
						url : Nexus.siesta.basePath + '/jqassistant/request-log',
						method : 'DELETE',
						suppressStatus : true,
						success : function() {
							me.showMessage('The request log has been cleared');
							store.load({
								params : {
									start : 0,
									limit : Nexus.jqassistant.store.RequestLog.PAGE_SIZE
								}
							});
						},
						failure : function(response) {
							me.showMessage('Failed to clear the request log: ' + me.parseExceptionMessage(response));
						}
					});
				}
			}
		});
	}

});