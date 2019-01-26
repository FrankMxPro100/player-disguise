package br.com.raphaelfury.disguise.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.raphaelfury.disguise.PlayerDisguise;
import br.com.raphaelfury.disguise.fake.FakeManager;
import br.com.raphaelfury.disguise.loader.ClassLoader;
import br.com.raphaelfury.disguise.mysql.MySQLManager;
import br.com.raphaelfury.disguise.utils.Utils;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class Manager {
	
	private PlayerDisguise plugin;
	private MySQLManager mysqlManager;
	private Utils utils;
	private FakeManager fakeManager;
	
	
	public Manager() {
		plugin = JavaPlugin.getPlugin(PlayerDisguise.class);
		plugin.saveDefaultConfig();
		utils = new Utils();
		
		mysqlManager = new MySQLManager(this);
		
		mysqlManager.getMySQL().createTables("CREATE TABLE IF NOT EXISTS `fake_names` (`names` TEXT NOT NULL)");
		mysqlManager.getMySQL().createTables("CREATE TABLE IF NOT EXISTS `skins_cache` (`id` INT PRIMARY KEY AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL DEFAULT '0', `value` BLOB, `signature` BLOB, `time` LONG);");
		
		fakeManager = new FakeManager(this);
		
		new ClassLoader(this).load();
		
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public FileConfiguration getConfig() {
		return getPlugin().getConfig();
	}

	public MySQLManager getMySQLManager() {
		return mysqlManager;
	}
	
	public FakeManager getFakeManager() {
		return fakeManager;
	}
	
	public Utils getUtils() {
		return utils;
	}
}
