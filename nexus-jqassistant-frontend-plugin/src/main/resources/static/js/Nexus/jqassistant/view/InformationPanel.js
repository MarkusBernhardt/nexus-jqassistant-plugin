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
 * jQAssistant information panel.
 */
NX.define('Nexus.jqassistant.view.InformationPanel', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.jqassistant.Icons' ],

	xtype : 'nx-jqassistant-view-information-panel',
	title : 'jQAssistant',
	cls : 'nx-jqassistant-view-information-panel',

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

		me.constructor.superclass.initComponent.apply(me, arguments);
	}
});