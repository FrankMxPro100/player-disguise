package br.com.raphaelfury.disguise;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.raphaelfury.disguise.manager.Manager;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class PlayerDisguise extends JavaPlugin {
	
	protected static Manager manager;
	
	@Override
	public void onEnable() {
		manager = new Manager();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

	public static Manager getManager() {
		return manager;
	}

}
