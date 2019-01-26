package br.com.raphaelfury.disguise.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import br.com.raphaelfury.disguise.PlayerDisguise;
import br.com.raphaelfury.disguise.utils.Utils;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class MySQL {
	
	private Utils utils;
	private String username, password, url;
	private Connection mainConnection, slaveConnection;

	public MySQL(Utils utils, String username, String password, String url) {
		this.utils = utils;
		this.username = username;
		this.password = password;
		this.url = url;
	}

	public Connection getConnection() {
		return mainConnection;
	}

	public Connection getSlaveConnection() {
		return slaveConnection;
	}
	
	public boolean openConnection() {
		try {
			mainConnection = DriverManager.getConnection(url, username, password);
			slaveConnection = DriverManager.getConnection(url, username, password);

			utils.log("Opening necessary connections in the database.", PlayerDisguise.getManager().getPlugin().getName());
			return true;
		} catch (Exception exception) {
			utils.error("There was an error while trying to open the required connections in the database.", PlayerDisguise.getManager().getPlugin().getName());
		}
		return false;
	}

	public synchronized void createTables(String table) {
		try {
			executeUpdate(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query) {
		try {
			if (getConnection().isClosed())
				utils.log("The connection is closed, so it was not possible to query ('" + query + "').", PlayerDisguise.getManager().getPlugin().getName());

			return getConnection().createStatement().executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void executeAsyncUpdate(String update) {
		try {
			Statement statement = getSlaveConnection().createStatement();
			statement.executeUpdate(update);
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void executeUpdate(String update) {
		try {
			Statement statement = getSlaveConnection().createStatement();
			statement.executeUpdate(update);
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
