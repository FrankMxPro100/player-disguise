package br.com.raphaelfury.disguise.loader;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.event.Listener;

import br.com.raphaelfury.disguise.command.manager.CustomCommand;
import br.com.raphaelfury.disguise.manager.Manager;
import br.com.raphaelfury.disguise.utils.ClassGetter;

/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class ClassLoader {

	public Manager manager;

	public ClassLoader(Manager manager) {
		this.manager = manager;
	}

	public void load() {
		manager.getUtils().log("Loading commands and listeners.", manager.getPlugin().getName());

		for (Class<?> classes : ClassGetter.getClassesForPackage(manager.getPlugin(), "br.com.raphaelfury.disguise")) {
			try {
				if (CustomCommand.class.isAssignableFrom(classes) && classes != CustomCommand.class) {
					CustomCommand command = (CustomCommand) classes.newInstance();
					if (command.enabled) {
						((CraftServer) Bukkit.getServer()).getCommandMap().register(command.getName(), command);
					} else {
						manager.getUtils().debug(classes.getSimpleName() + " command found but not loaded.", manager.getPlugin().getName());
					}
				}
			} catch (Exception exception) {
				manager.getUtils().error("An error occurred while trying to load " + classes.getSimpleName() + " command.", manager.getPlugin().getName(), exception);
			}
			try {
				if (Listener.class.isAssignableFrom(classes)) {
					Listener listener = (Listener) classes.newInstance();
					Bukkit.getPluginManager().registerEvents(listener, manager.getPlugin());
				}
			} catch (Exception exception) {
				manager.getUtils().error("An error occurred while trying to load " + classes.getSimpleName() + " listener.", manager.getPlugin().getName(), exception);
			}
		}
	}

}
