package br.com.raphaelfury.disguise.permissions;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class PermissionsManager {
	
	private static final String PLUGIN_PREFIX = "disguise.";
	
	public static String getPluginPermission() {
		return PLUGIN_PREFIX;
	}
	
	public static String getSpecialPermission(String permission) {
		return PLUGIN_PREFIX + permission;
	}
	
	public static String getCommandPermission(String command) {
		return PLUGIN_PREFIX + "cmd." + command;
	}
	
	public static String getFakeCommandPermission(String permission) {
		return getCommandPermission("fake." + permission);
	}

}
