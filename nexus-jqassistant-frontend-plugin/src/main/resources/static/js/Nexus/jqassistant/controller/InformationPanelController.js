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
 * JQAssistant information panel controller.
 */
NX.define('Nexus.jqassistant.controller.InformationPanelController', {
	extend : 'Nexus.controller.Controller',

	requires : [ 'Nexus.siesta', 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.view.InformationPanel' ],

	init : function() {
		var me = this;

		me.addInformationPanel();
	},

	/**
	 * @private
	 */
	addInformationPanel : function() {
		var me = this;

		Sonatype.Events.on('fileContainerInit', function(items) {
			me.createInformationPanel(items);
		});

		Sonatype.Events.on('fileContainerUpdate', function(container, data) {
			me.updateInformationPanel(container, data);
		});

		Sonatype.Events.on('artifactContainerInit', function(items) {
			me.createInformationPanel(items);
		});

		Sonatype.Events.on('artifactContainerUpdate', function(container, data) {
			me.updateInformationPanel(container, data);
		});

	},

	/**
	 * @private
	 */
	createInformationPanel : function(items) {
		items.push(Ext.create({
			xtype : 'nx-jqassistant-view-information-panel',
			name : 'nx-jqassistant-view-information-panel'
		}));
	},

	/**
	 * @private
	 */
	updateInformationPanel : function(container, data) {
		var me = this;
		var panel = container.find('name', 'nx-jqassistant-view-information-panel')[0];

		if (data && data.leaf && data.resourceURI) {
//			Ext.Ajax.request({
//				url : data.resourceURI + '?describe=maven2&isLocal=true',
//				method : 'GET',
//				scope : me,
//				suppressStatus : 404,
//				callback : function(options, isSuccess, response) {
//					if (isSuccess) {
//						me.logDebug('Information Panel: ' + response.responseText);
//						var values = Ext.decode(response.responseText);
//			me.loadInformationPanel(container, panel, values.data);
			me.loadInformationPanel(container, panel, data.resourceURI);
//					} else {
//						container.hideTab(this);
//						if (response.status != 404) {
//							Sonatype.utils.connectionError(response, 'Unable to retrieve Maven information.');
//						}
//					}
//				},
//			});
		} else {
			container.hideTab(panel);
		}
	},

	/**
	 * @private
	 */
	loadInformationPanel : function(container, panel, data) {
		var me = this;

		me.logDebug('Loading information panel');

		Ext.Ajax.request({
			url : Nexus.siesta.basePath + '/jqassistant/information-panel',
			method : 'POST',
//			jsonData : data,
			jsonData : {
				resourceURI: data
			},

			scope : me,
			success : function(response, opts) {
				me.logDebug('Information Panel: ' + response.responseText);
				var values = Ext.decode(response.responseText);

				// panel.setValues(values);

				if (values.activated === true) {
					container.showTab(panel);
				} else {
					container.hideTab(panel);
				}
			}
		});
	},

	/**
	 * @private
	 */
	showMessage : function(message) {
		Nexus.messages.show('jQAssistant', message);
	}

});