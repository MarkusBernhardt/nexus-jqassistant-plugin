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
 * Container for icons used by jqassistant plugin.
 */
NX.define('Nexus.jqassistant.Icons', {
	extend : 'Nexus.util.IconContainer',
	singleton : true,

	/**
	 * @constructor
	 */
	constructor : function() {
		var me = this;

		// helper to build an icon config with variants, where variants live in
		// directories, foo.png x16 -> x16/foo.png
		function iconConfig(fileName, variants) {
			var config = {};
			if (variants === undefined) {
				variants = [ 'x32', 'x16' ];
			}
			Ext.each(variants, function(variant) {
				config[variant] = variant + '/' + fileName;
			});
			return config;
		}

		me.constructor.superclass.constructor.call(me, {
			stylePrefix : 'nx-jqassistant-icon-',

			icons : {
				jqa : 'jqa.png',
				jqa_full : 'jqa_full.png',
				refresh : 'refresh.png',
				clear : iconConfig('clear.png')
			}
		});
	}
});