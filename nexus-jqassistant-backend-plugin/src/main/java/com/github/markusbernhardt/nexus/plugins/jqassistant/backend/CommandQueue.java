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
package com.github.markusbernhardt.nexus.plugins.jqassistant.backend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.EventSubscriber;

@Named
@Singleton
public class CommandQueue implements Runnable, EventSubscriber {

	/**
	 * The plugin context
	 */
	protected final BackendPluginContext backendPluginContext;

	/**
	 * The capacity of the command queue;
	 */
	protected int commandQueueSize;

	/**
	 * The queue to cache pending commands
	 */
	protected BlockingQueue<Command> queue;

	/**
	 * This volatile flag indicates whether the worker should stop
	 */
	protected volatile boolean stop = false;

	/**
	 * The worker is executing the commands with this thread
	 */
	protected Thread thread = null;

	@Inject
	public CommandQueue(BackendPluginContext backendPluginContext) {
		this.backendPluginContext = backendPluginContext;
		this.commandQueueSize = backendPluginContext.getSettingsProvider().getSettings().getCommandQueueSize();
		this.queue = new ArrayBlockingQueue<>(commandQueueSize);
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Command command = queue.take();
				command.execute();
			} catch (InterruptedException e) {
			} catch (RuntimeException e) {
				backendPluginContext.getLogger().error(e.getMessage(), e);
			}
		}
	}

	public void start() {
		stop = false;
		thread = new Thread(this);
		thread.setContextClassLoader(this.getClass().getClassLoader());
		thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				backendPluginContext.getLogger().error(e.getMessage(), e);
			}
		});
		thread.start();
	}

	public void stop() {
		enqueueCommand(new StopCommand());

		boolean flag = true;
		while (flag) {
			try {
				thread.join();
				flag = false;
			} catch (InterruptedException e) {
			}
		}

	}

	public void resize(int newCommandQueueSize) {
		if (commandQueueSize != newCommandQueueSize) {
			enqueueCommand(new ResizeCommand(newCommandQueueSize));
		}
	}

	public void enqueueCommand(Command command) {
		while (command != null) {
			try {
				queue.put(command);
				command = null;
			} catch (InterruptedException ex) {
			}
		}
	}

	public int getCommandQueueSize() {
		return commandQueueSize;
	}

	protected void stopThreadSyncPart() {
		stop = true;
	}

	protected void stopThreadAsyncPart() {
		thread.interrupt();

		boolean flag = true;
		while (flag) {
			try {
				thread.join();
				flag = false;
			} catch (InterruptedException e) {
			}
		}
	}

	protected class StopCommand implements Command {
		@Override
		public void execute() {
			stopThreadSyncPart();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					stopThreadAsyncPart();
				}
			});
			t.start();
		}
	}

	protected class ResizeCommand implements Command {
		protected final int newCommandQueueSize;

		public ResizeCommand(int newCommandQueueSize) {
			super();
			this.newCommandQueueSize = newCommandQueueSize;
		}

		@Override
		public void execute() {
			stopThreadSyncPart();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					stopThreadAsyncPart();
					BlockingQueue<Command> queueOld = queue;
					commandQueueSize = newCommandQueueSize;
					queue = new ArrayBlockingQueue<>(commandQueueSize);
					start();
					for (Command command : queueOld) {
						enqueueCommand(command);
					}
				}
			});
			t.start();
		}
	}
}
