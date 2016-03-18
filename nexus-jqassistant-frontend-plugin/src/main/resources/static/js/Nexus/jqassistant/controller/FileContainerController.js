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
 * JQAssistant file container controller.
 */
NX.define('Nexus.jqassistant.controller.FileContainerController', {
	extend : 'Nexus.controller.Controller',

	requires : [ 'Nexus.siesta', 'Nexus.jqassistant.Icons' ],

	init : function() {
		var me = this;

		me.addContainerTab();
	},

	/**
	 * @private
	 */
	addContainerTab : function() {
		var me = this;

		Sonatype.Events.on('fileContainerInit', function(items) {
			me.showMessage('fileContainerInit');
		});
		Sonatype.Events.on('fileContainerUpdate', function(items) {
			me.showMessage('fileContainerUpdate');
		});
	},

	/**
	 * @private
	 */
	showMessage : function(message) {
		Nexus.messages.show('jQAssistant', message);
	}

});