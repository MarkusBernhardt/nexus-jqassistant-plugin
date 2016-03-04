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

/**
 * jQAssistant administration panel.
 */
NX.define('Nexus.jqassistant.view.AdministrationPanel', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.view.Settings', 'Nexus.jqassistant.view.ModelLog', 'Nexus.jqassistant.view.ArtifactLog',
		'Nexus.jqassistant.view.RequestLog' ],

	xtype : 'nx-jqassistant-view-administration-panel',
	title : 'jQAssistant',
	cls : 'nx-jqassistant-view-administration-panel',

	border : false,
	layout : {
		type : 'vbox',
		align : 'stretch'
	},

	/**
	 * @override
	 */
	initComponent : function() {
		var me = this, icons = Nexus.jqassistant.Icons;

		Ext.apply(me, {
			items : [ {
				xtype : 'panel',
				cls : 'nx-jqassistant-view-administration-panel-description',
				html : icons.get('jqa_full').img,
				border : false
			}, {
				xtype : 'tabpanel',
				flex : 1,
				border : false,
				plain : true,
				layoutOnTabChange : true,
				items : [ {
					xtype : 'nx-jqassistant-view-settings'
				}, {
					xtype : 'nx-jqassistant-view-model-log'
				}, {
					xtype : 'nx-jqassistant-view-artifact-log'
				}, {
					xtype : 'nx-jqassistant-view-request-log'
				} ],
				activeTab : 0,

				listeners : {
					afterrender : function(tabpanel) {
						// default to have the log tabs hidden
						tabpanel.hideTabStripItem(me.down('nx-jqassistant-view-model-log'));
						tabpanel.hideTabStripItem(me.down('nx-jqassistant-view-artifact-log'));
						tabpanel.hideTabStripItem(me.down('nx-jqassistant-view-request-log'));
					}
				}
			} ]
		});

		me.constructor.superclass.initComponent.apply(me, arguments);
	}
});