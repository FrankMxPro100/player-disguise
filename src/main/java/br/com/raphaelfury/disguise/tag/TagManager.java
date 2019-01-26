package br.com.raphaelfury.disguise.tag;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class TagManager {

	public static HashMap<UUID, String> displayName = new HashMap<>();
	public static HashMap<UUID, String> prefixRank = new HashMap<>();

	public static String getDisplayName(Player player) {
		if (!displayName.containsKey(player.getUniqueId())) {
			setDisplayName(player, "�7" + player.getName());
		}
		return displayName.get(player.getUniqueId());
	}

	public static String getPrefixRank(Player player) {
		if (!prefixRank.containsKey(player.getUniqueId())) {
			setPrefixRank(player, "�7");
		}
		return prefixRank.get(player.getUniqueId());
	}

	public static void setDisplayName(Player player, String name) {
		if (displayName.containsKey(player.getUniqueId())) {
			removeDisplayName(player);
		}

		displayName.put(player.getUniqueId(), name);
	}

	public static void setPrefixRank(Player player, String name) {
		if (prefixRank.containsKey(player.getUniqueId())) {
			removePrefixRank(player);
		}

		prefixRank.put(player.getUniqueId(), name);
	}

	public static void removeDisplayName(Player player) {
		if (displayName.containsKey(player.getUniqueId())) {
			displayName.remove(player.getUniqueId());
		}
	}

	public static void removePrefixRank(Player player) {
		if (prefixRank.containsKey(player.getUniqueId())) {
			prefixRank.remove(player.getUniqueId());
		}
	}

}
