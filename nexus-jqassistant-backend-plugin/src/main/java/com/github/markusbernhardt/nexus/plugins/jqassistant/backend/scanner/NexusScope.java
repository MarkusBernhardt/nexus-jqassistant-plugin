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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;

public enum NexusScope implements Scope {

	SCAN {
		@Override
		public void onEnter(ScannerContext context) {
		}

		@Override
		public void onLeave(ScannerContext context) {
		}
	},
	LOG {
		@Override
		public void onEnter(ScannerContext context) {
		}

		@Override
		public void onLeave(ScannerContext context) {
		}
	};

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getPrefix() {
		return "nexus";
	}
}
