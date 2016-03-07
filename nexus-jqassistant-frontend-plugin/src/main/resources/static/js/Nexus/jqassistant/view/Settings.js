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
 * Settings panel.
 */
NX.define('Nexus.jqassistant.view.Settings', {
	extend : 'Ext.Panel',

	mixins : [ 'Nexus.LogAwareMixin' ],

	xtype : 'nx-jqassistant-view-settings',
	title : 'Settings',
	id : 'nx-jqassistant-view-settings',
	cls : 'nx-jqassistant-view-settings',

	border : false,
	// layout: 'fit',

	/**
	 * @override
	 */
	initComponent : function() {
		var me = this;

		Ext.apply(me, {
			items : [ {
				xtype : 'form',
				region : 'center',
				trackResetOnLoad : true,
				autoScroll : true,
				border : false,
				frame : true,
				collapsible : false,
				collapsed : false,
				labelWidth : 175,
				layoutConfig : {
					labelSeparator : ''
				},

				items : [ {
					xtype : 'fieldset',
					checkboxToggle : false,
					title : 'jQAssistant',
					anchor : Sonatype.view.FIELDSET_OFFSET_WITH_SCROLL,
					collapsible : true,
					autoHeight : true,
					layoutConfig : {
						labelSeparator : ''
					},

					items : [ {
						xtype : 'checkbox',
						fieldLabel : 'Activate jQAssistant',
						helpText : 'Check this box to activate the jQAssistant plug in.',
						name : 'activated',
						inputValue : 'true',
						itemCls : 'required-field',
						allowBlank : false
					}, {
						id : 'nx-jqassistant-view-settings-form-full-scan',
						xtype : 'checkbox',
						fieldLabel : 'Enable full scan',
						helpText : 'Check this box to enable the full scanning of all stored items (jars, ears, zips, Java classes, etc.).',
						name : 'fullScan',
						inputValue : 'true',
						itemCls : 'required-field',
						allowBlank : false
					}, {
						id : 'nx-jqassistant-view-settings-form-command-queue-size',
						xtype : 'numberfield',
						fieldLabel : 'Command queue size',
						helpText : 'The maximum number of commands to cache before blocking the internal queue. (Minimum is 100)',
						name : 'commandQueueSize',
						anchor : Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
						allowBlank : false,
						itemCls : 'required-field'
					} ]
				}, {
					xtype : 'fieldset',
					checkboxToggle : false,
					title : 'Logging',
					anchor : Sonatype.view.FIELDSET_OFFSET_WITH_SCROLL,
					collapsible : true,
					autoHeight : true,
					layoutConfig : {
						labelSeparator : ''
					},

					items : [ {
						id : 'nx-jqassistant-view-settings-form-model-log-size',
						xtype : 'numberfield',
						fieldLabel : 'Model log size',
						helpText : 'The maximum size of the model log.',
						name : 'modelLogSize',
						anchor : Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
						allowBlank : false,
						itemCls : 'required-field'
					}, {
						id : 'nx-jqassistant-view-settings-form-artifact-log-size',
						xtype : 'numberfield',
						fieldLabel : 'Artifact log size',
						helpText : 'The maximum size of the artifact log.',
						name : 'artifactLogSize',
						anchor : Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
						allowBlank : false,
						itemCls : 'required-field'
					}, {
						id : 'nx-jqassistant-view-settings-form-request-log-size',
						xtype : 'numberfield',
						fieldLabel : 'Request log size',
						helpText : 'The maximum size of the request log.',
						name : 'requestLogSize',
						anchor : Sonatype.view.FIELD_OFFSET_WITH_SCROLL,
						allowBlank : false,
						itemCls : 'required-field'
					} ]
				} ],

				buttonAlign : 'left',
				buttons : [ {
					xtype : 'button',
					text : 'Save',
					id : 'nx-jqassistant-view-settings-button-save',
					formBind : true
				} ]
			} ]
		});

		me.constructor.superclass.initComponent.apply(me, arguments);
	},

	/**
	 * Get form values.
	 * 
	 * @public
	 */
	getValues : function() {
		return this.down('form').getForm().getValues();
	},

	/**
	 * Set form values.
	 * 
	 * @public
	 */
	setValues : function(values) {
		this.down('form').getForm().setValues(values);
	}
});