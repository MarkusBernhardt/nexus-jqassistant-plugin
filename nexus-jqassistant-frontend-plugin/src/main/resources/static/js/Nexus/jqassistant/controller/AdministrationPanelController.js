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
						id : 'jqassistant'
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

				var tabPanel = Ext.getCmp('jqassistant').down('tabpanel');
				var modelLogView = Ext.getCmp('nx-jqassistant-view-model-log');

				var settingsFullScanField = Ext.getCmp('nx-jqassistant-view-settings-form-full-scan');
				var settingsModelLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-model-log-size');
				var settingsArtifactLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-artifact-log-size');
				var settingsRequestLogSizeField = Ext.getCmp('nx-jqassistant-view-settings-form-request-log-size');
				if (values.activated === true) {
					tabPanel.unhideTabStripItem(modelLogView);

					settingsFullScanField.setDisabled(false);
					settingsModelLogSizeField.setDisabled(false);
					settingsArtifactLogSizeField.setDisabled(false);
					settingsRequestLogSizeField.setDisabled(false);
				} else {
					tabPanel.hideTabStripItem(modelLogView);

					settingsFullScanField.setDisabled(true);
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
			msg : 'Clear the model scan log?',
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
	}

});