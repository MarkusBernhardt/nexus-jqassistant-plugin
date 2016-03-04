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
 * ArtifactLog panel.
 */
NX.define('Nexus.jqassistant.view.ArtifactLog', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.store.ArtifactLog' ],

	xtype : 'nx-jqassistant-view-artifact-log',
	title : 'Artifact Log',
	id : 'nx-jqassistant-view-artifact-log',
	cls : 'nx-jqassistant-view-artifact-log',

	border : false,
	layout : 'fit',

	/**
	 * @override
	 */
	initComponent : function() {
		var me = this, icons = Nexus.jqassistant.Icons, store = NX.create('Nexus.jqassistant.store.ArtifactLog'), expander = NX.create('Ext.ux.grid.RowExpander',
			{
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
								name : 'Duration',
								value : values.duration
							});							
							result.push({
								name : 'Created At',
								value : values.createdAt
							});
							result.push({
								name : 'Created By IP',
								value : values.createdByAddress
							});
							result.push({
								name : 'Created By User',
								value : values.createdByUser
							});
							result.push({
								name : 'Last Updated At',
								value : values.lastUpdatedAt
							});
							result.push({
								name : 'Last Updated By IP',
								value : values.lastUpdatedByAddress
							});
							result.push({
								name : 'Last Updated By User',
								value : values.lastUpdatedByUser
							});
							result.push({
								name : 'Last Requested At',
								value : values.lastRequestedAt
							});
							result.push({
								name : 'Last Requested By IP',
								value : values.lastRequestedByAddress
							});
							result.push({
								name : 'Last Requested By User',
								value : values.lastRequestedByUser
							});
							result.push({
								name : 'Request Count',
								value : values.requestCount
							});
							result.push({
								name : 'Deep Scan',
								value : values.deepScan
							});
							result.push({
								name : 'Duration',
								value : values.duration
							});
							result.push({
								name : 'Path',
								value : values.filename
							});
							result.push({
								name : 'Descriptors',
								value : values.descriptors
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
				emptyText : 'No logged artifacts',
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
				pageSize : Nexus.jqassistant.store.ArtifactLog.PAGE_SIZE,
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
				id : 'nx-jqassistant-view-artifact-log-button-refresh',
				text : 'Refresh',
				tooltip : 'Refresh artifact log',
				iconCls : icons.get('refresh').cls
			}, {
				xtype : 'button',
				id : 'nx-jqassistant-view-artifact-log-button-clear',
				text : 'Clear',
				tooltip : 'Clear artifact log',
				iconCls : icons.get('clear').cls
			}, '->', {
				xtype : 'nx-grid-filter-box',
				filteredGrid : me.grid
			} ]
		});

		me.constructor.superclass.initComponent.apply(me, arguments);
	},

	/**
	 * Returns the artifact log grid.
	 * 
	 * @public
	 */
	getGrid : function() {
		return this.grid;
	}
});