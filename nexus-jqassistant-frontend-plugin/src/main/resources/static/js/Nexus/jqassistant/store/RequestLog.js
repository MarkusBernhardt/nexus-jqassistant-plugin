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

/*global NX, Ext, Nexus*/
/**
 * RequestLog store.
 */
NX.define('Nexus.jqassistant.store.RequestLog', {
	extend : 'Ext.data.Store',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.siesta' ],

	statics : {
		/**
		 * @public
		 */
		PAGE_SIZE : 250
	},

	/**
	 * @constructor
	 */
	constructor : function() {
		var me = this;

		Ext.apply(me, {
			storeId : 'nx-jqassistant-store-request-log',
			autoDestroy : true,
			restful : true,

			proxy : NX.create('Ext.data.HttpProxy', {
				url : Nexus.siesta.basePath + '/jqassistant/request-log'
			}),

			reader : NX.create('Ext.data.JsonReader', {
				root : 'rows',
				totalProperty : 'total',
				fields : [ 'sequence', 'timestamp', 'uid', 'repository', 'groupId', 'artifactId', 'version', 'classifier', 'extension', 'filename',
					'requestedAt', 'requestedByAddress', 'requestedByUser', 'requestCount' ]
			})
		});

		me.constructor.superclass.constructor.call(me);
	}
});