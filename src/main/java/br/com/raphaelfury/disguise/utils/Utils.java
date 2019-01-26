package br.com.raphaelfury.disguise.utils;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class Utils {

	public boolean debugMode = true;

	public void log(Object object, String plugin) {
		Bukkit.getServer().getConsoleSender().sendMessage("§a(" + plugin + ") " + object);
	}

	public void debug(Object object, String plugin) {
		if (debugMode) {
			Bukkit.getServer().getConsoleSender().sendMessage("§e(" + plugin + "-debug) §7" + object);
		}
	}

	public void error(Object object, String plugin) {
		Bukkit.getServer().getConsoleSender().sendMessage("§4" + plugin + " - " + object);
	}

	public void error(Object object, String plugin, Exception exception) {
		Bukkit.getServer().getConsoleSender().sendMessage("§4(" + plugin + ") " + object);
		Bukkit.getServer().getConsoleSender().sendMessage("§4Error - " + exception.getMessage());
	}

	public String formatTime(int i) {
		if (i >= 60) {
			int minutes = i / 60;
			int seconds = i - minutes * 60;
			if (seconds == 0) {
				if (minutes > 1) {
					return minutes + " minutos";
				} else {
					return minutes + " minuto";
				}
			}
			String min = "minuto";
			String second = "segundo";
			if (minutes > 1)
				min = min + "s";
			if (seconds > 1)
				second = second + "s";
			return minutes + " " + min + " e " + seconds + " " + second;
		}
		if (i > 1)
			return i + " segundos";
		return i + " segundo";
	}

	public String toTime(int i) {
		int minutes = i / 60;
		int seconds = i % 60;

		if (minutes > 0) {
			return minutes + "min" + (seconds > 0 ? " " + seconds + "s" : "");
		} else {
			return seconds + "s";
		}
	}

	public String compareTime(long current, long end) {
		long ms = end - current;
		int seconds = (int) (ms / 1000) % 60;
		int minutes = (int) (ms / (1000 * 60)) % 60;
		int hours = (int) (ms / (1000 * 60 * 60) % 24);
		int days = (int) (ms / (1000 * 60 * 60 * 24));

		return (days > 0 ? days + " dias " : "") + (hours > 0 ? hours + " horas " : "") + (minutes > 0 ? minutes + " minutos " : "") + (seconds > 0 ? seconds + " segundos" : "");
	}

	public UUID fromRenato(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
	}

	public String captalize(String toCaptalize) {
		return toCaptalize.substring(0, 1).toUpperCase() + toCaptalize.substring(1);
	}
	
	public char randomChar() {
		return (char) ('a' + new Random().nextInt(25));
	}

	public char nextChar(int id) {
		return (char) ('a' + id);
	}
	
}
