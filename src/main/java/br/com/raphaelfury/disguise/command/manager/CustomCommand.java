package br.com.raphaelfury.disguise.command.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.raphaelfury.disguise.PlayerDisguise;
import br.com.raphaelfury.disguise.manager.Manager;
import br.com.raphaelfury.disguise.permissions.PermissionsManager;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public abstract class CustomCommand extends Command {

	private Manager manager;
	public boolean enabled = true;

	public CustomCommand(String name) {
		super(name);
	}

	public CustomCommand(String name, String description) {
		super(name, description, "", new ArrayList<>());
	}

	public CustomCommand(String name, String description, List<String> aliases) {
		super(name, description, "", aliases);
	}

	@Override
	public abstract boolean execute(CommandSender commandSender, String label, String[] args);

	public Manager getManager() {
		this.manager = PlayerDisguise.getManager();
		return manager;
	}

	public Integer getInteger(String string) {
		return Integer.valueOf(string);
	}

	public boolean hasPermission(CommandSender commandSender, String perm) {
		return commandSender.hasPermission(PermissionsManager.getCommandPermission(perm));
	}

	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	public boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public String getArgs(String[] args, int starting) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = starting; i < args.length; i++) {
			stringBuilder.append(args[i] + " ");
		}
		return stringBuilder.toString();
	}

	public void sendNumericMessage(CommandSender sender) {
		sender.sendMessage("number-error");
	}

	public void sendPermissionMessage(CommandSender sender) {
		sender.sendMessage("§cVocê não possui permissão para utilizar este comando.");
	}

	public void sendExecutorMessage(CommandSender sender) {
		sender.sendMessage("§cEste comando é exclusivo para jogadores.");
	}

}
