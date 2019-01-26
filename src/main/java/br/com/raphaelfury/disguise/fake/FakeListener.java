package br.com.raphaelfury.disguise.fake;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.raphaelfury.disguise.PlayerDisguise;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class FakeListener implements Listener {

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		PlayerDisguise.getManager().getFakeManager().removeFake(event.getPlayer().getUniqueId());
	}
	
}
