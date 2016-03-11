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
 * RequestLog panel.
 */
NX.define('Nexus.jqassistant.view.RequestLog', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.store.RequestLog' ],

	xtype : 'nx-jqassistant-view-request-log',
	title : 'Request Log',
	id : 'nx-jqassistant-view-request-log',
	cls : 'nx-jqassistant-view-request-log',

	border : false,
	layout : 'fit',

	/**
	 * @override
	 */
	initComponent : function() {
		var me = this, icons = Nexus.jqassistant.Icons, store = NX.create('Nexus.jqassistant.store.RequestLog'), expander = NX.create(
			'Ext.ux.grid.RowExpander', {
				tpl : new Ext.XTemplate('<table style="padding: 5px;">', '<tpl for="this.attributes(values)">', '<tr>',
					'<td style="padding-right: 5px;"><b>{name}</b></td>', '<td>{value}</td>', '</tr>', '</tpl>', '</table>', {
						compiled : true,

						attributes : function(values) {
							var result = [];
							result.push({
								name : 'UID',
								value : values.uid
							});
							result.push({
								name : 'Path',
								value : values.filename
							});
							result.push({
								name : 'Requested At',
								value : values.requestedAt
							});
							result.push({
								name : 'Requested By IP',
								value : values.requestedByAddress
							});
							result.push({
								name : 'Requested By User',
								value : values.requestedByUser
							});
							result.push({
								name : 'Request Count',
								value : values.requestCount
							});
							return result;
						}
					})
			});

		me.grid = NX.create('Ext.grid.GridPanel', {
			border : false,
			autoScroll : true,

			loadMask : {
				msg : 'Loading...',
				msgCls : 'loading-indicator'
			},

			store : store,

			viewConfig : {
				emptyText : 'No logged requests',
				deferEmptyText : false
			},

			stripeRows : true,
			autoExpandColumn : 'version',

			colModel : NX.create('Ext.grid.ColumnModel', {
				defaults : {
					sortable : true
				},
				columns : [ expander, {
					id : 'sequence',
					header : 'Sequence',
					dataIndex : 'sequence',
					width : 75
				}, {
					id : 'timestamp',
					header : 'Timestamp',
					dataIndex : 'timestamp',
					width : 125
				}, {
					id : 'repository',
					header : 'Repository',
					dataIndex : 'repository',
					width : 150
				}, {
					id : 'groupId',
					header : 'GroupId',
					dataIndex : 'groupId',
					width : 200
				}, {
					id : 'artifactId',
					header : 'ArtifactId',
					dataIndex : 'artifactId',
					width : 200
				}, {
					id : 'classifier',
					header : 'Classifier',
					dataIndex : 'classifier',
					width : 100
				}, {
					id : 'extension',
					header : 'Extension',
					dataIndex : 'extension',
					width : 75
				}, {
					id : 'version',
					header : 'Version',
					dataIndex : 'version',
				} ]
			}),

			plugins : [ expander ],

			bbar : NX.create('Ext.PagingToolbar', {
				pageSize : Nexus.jqassistant.store.RequestLog.PAGE_SIZE,
				store : store,
				displayInfo : true,
				displayMsg : 'Displaying logs {0} - {1} of {2}',
				emptyMsg : 'No logs to display'
			})
		});

		Ext.apply(me, {
			items : [ me.grid ],

			tbar : [ {
				xtype : 'button',
				id : 'nx-jqassistant-view-request-log-button-refresh',
				text : 'Refresh',
				tooltip : 'Refresh request log',
				iconCls : icons.get('refresh').cls
			}, {
				xtype : 'button',
				id : 'nx-jqassistant-view-request-log-button-clear',
				text : 'Clear',
				tooltip : 'Clear request log',
				iconCls : icons.get('clear').cls
			}, '->', {
				xtype : 'nx-grid-filter-box',
				filteredGrid : me.grid
			} ]
		});

		me.constructor.superclass.initComponent.apply(me, arguments);
	},

	/**
	 * Returns the request log grid.
	 * 
	 * @public
	 */
	getGrid : function() {
		return this.grid;
	}
});