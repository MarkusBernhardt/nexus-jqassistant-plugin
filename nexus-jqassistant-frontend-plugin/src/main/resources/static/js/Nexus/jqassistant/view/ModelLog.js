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
 * ModelLog panel.
 */
NX.define('Nexus.jqassistant.view.ModelLog', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	requires : [ 'Nexus.jqassistant.Icons', 'Nexus.jqassistant.store.ModelLog' ],

	xtype : 'nx-jqassistant-view-model-log',
	title : 'Model Log',
	id : 'nx-jqassistant-view-model-log',
	cls : 'nx-jqassistant-view-model-log',

	border : false,
	layout : 'fit',

	/**
	 * @override
	 */
	initComponent : function() {
		var me = this, icons = Nexus.jqassistant.Icons, store = NX.create('Nexus.jqassistant.store.ModelLog'), expander = NX.create('Ext.ux.grid.RowExpander',
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
				emptyText : 'No logged models',
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
					width : 175
				}, {
					id : 'groupId',
					header : 'GroupId',
					dataIndex : 'groupId',
					width : 250
				}, {
					id : 'artifactId',
					header : 'ArtifactId',
					dataIndex : 'artifactId',
					width : 250
				}, {
					id : 'version',
					header : 'Version',
					dataIndex : 'version',
				} ]
			}),

			plugins : [ expander ],

			bbar : NX.create('Ext.PagingToolbar', {
				pageSize : Nexus.jqassistant.store.ModelLog.PAGE_SIZE,
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
				id : 'nx-jqassistant-view-model-log-button-refresh',
				text : 'Refresh',
				tooltip : 'Refresh model log',
				iconCls : icons.get('refresh').cls
			}, {
				xtype : 'button',
				id : 'nx-jqassistant-view-model-log-button-clear',
				text : 'Clear',
				tooltip : 'Clear model log',
				iconCls : icons.get('clear').cls
			}, '->', {
				xtype : 'nx-grid-filter-box',
				filteredGrid : me.grid
			} ]
		});

		me.constructor.superclass.initComponent.apply(me, arguments);
	},

	/**
	 * Returns the model log grid.
	 * 
	 * @public
	 */
	getGrid : function() {
		return this.grid;
	}
});