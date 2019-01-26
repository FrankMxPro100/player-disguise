package br.com.raphaelfury.disguise.mysql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.raphaelfury.disguise.manager.Manager;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class MySQLManager {

	private Manager manager;
	private MySQL mysql;
	private String prefixMysql;

	public MySQLManager(Manager manager) {
		this.manager = manager;

		this.mysql = new MySQL(manager.getUtils(), manager.getConfig().get("mysql.user").toString(), manager.getConfig().get("mysql.pass").toString(), manager.getConfig().get("mysql.url").toString());
		mysql.openConnection();
		prefixMysql = manager.getConfig().get("mysql.prefix").toString();
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public void playerExists(UUID uuid, String table, Callback<Boolean> callback) {
		try {
			ResultSet resultSet = getMySQL().executeQuery("SELECT * FROM " + table + " WHERE uuid= '" + uuid + "';");
			callback.finish(resultSet.next());
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean contains(String table, String where, String whereTo) {
		try {
			ResultSet set = manager.getMySQLManager().getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
			if (!set.next())
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void updateFakeNames(String name) {
		if (getString("fake_names", "names").isEmpty()) {
			getMySQL().executeAsyncUpdate("INSERT INTO `fake_names` (`names`) VALUES ('" + name + "');");
		} else {
			getMySQL().executeAsyncUpdate("UPDATE `fake_names` SET `names`='" + getString("fake_names", "names") + "," + name + "';");
		}
	}
	
	public void replaceFakeName(String oldName, String name) {
		getMySQL().executeAsyncUpdate("UPDATE `fake_names` SET `names`='" + getString("fake_names", "names").replace(oldName, name) + "';");
	}
	
	public void removeFakeName(String name) {
		getMySQL().executeAsyncUpdate("UPDATE `fake_names` SET `names`='" + getString("fake_names", "names").replace("," + name, "").replace(name, "") + "';");
	}
	
	public void updateSkin(UUID uniqueId, String value, String signature) {
		if (!contains("skins_cache", "uuid", uniqueId.toString())) {
			getMySQL().executeAsyncUpdate("INSERT INTO `skins_cache` (`uuid`, `value`, `signature`, `time`) VALUES ('" + uniqueId + "', '" + value + "', '" + signature + "', '" + System.currentTimeMillis() + "');");
		} else {
			getMySQL().executeAsyncUpdate("UPDATE `skins_cache` SET `value`='" + value + "', `signature`='" + signature + "', `time`='" + System.currentTimeMillis() + "' WHERE `uuid`='" + uniqueId + "';");
		}
	}

	public String getString(String table, String where, String whereTo, String column) {
		try {
			ResultSet set = manager.getMySQLManager().getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
			if (!set.next())
				return new String();
			return set.getString(column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getString(String table, String column) {
		try {
			ResultSet set = manager.getMySQLManager().getMySQL().executeQuery("SELECT * FROM `" + table + ";");
			if (!set.next())
				return new String();
			return set.getString(column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer getInteger(String table, String where, String whereTo, String column) {
		try {
			ResultSet set = getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
			if (!set.next())
				return 0;
			return set.getInt(column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long getLong(String table, String where, String whereTo, String column) {
		try {
			ResultSet set = getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
			if (!set.next())
				return 0;
			return set.getLong(column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getLastID(String table) {
		ResultSet rs = getMySQL().executeQuery("SELECT `id` FROM `" + table + "` ORDER BY `id` DESC LIMIT 1");
		int id = 0;
		try {
			if (rs.next())
				id = rs.getInt("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id + 1;
	}

	public List<UUID> getList(String table, String where, String whereTo, String column) {
		List<UUID> objects = new ArrayList<>();
		try {
			ResultSet set = manager.getMySQLManager().getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
			if (set.next())
				objects.add(UUID.fromString(set.getString(column)));
			set.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objects;
	}

	public ResultSet getResultSet(String table, String where, String whereTo) {
		ResultSet set = null;
		try {
			set = manager.getMySQLManager().getMySQL().executeQuery("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + whereTo + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	public String getPrefixMysql() {
		return prefixMysql;
	}
}
