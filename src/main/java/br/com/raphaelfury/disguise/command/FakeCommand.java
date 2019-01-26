package br.com.raphaelfury.disguise.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.raphaelfury.disguise.command.manager.CustomCommand;
import br.com.raphaelfury.disguise.fake.FakeManager;
import br.com.raphaelfury.disguise.permissions.PermissionsManager;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class FakeCommand extends CustomCommand {

	private FakeManager fakeManager;
	private ArrayList<String> randomNames;

	public FakeCommand() {
		super("fake", "Utilize o nome e skin de um outro jogador.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (fakeManager == null || randomNames == null) {
			fakeManager = getManager().getFakeManager();
			randomNames = new ArrayList<>();
			
			if (!getManager().getMySQLManager().getString("fake_names", "names").isEmpty())
				randomNames.addAll(Arrays.asList(getManager().getMySQLManager().getString("fake_names", "names").split(",")));
		}
		
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return true;
		}

		if (!hasPermission(commandSender, "fake")) {
			sendPermissionMessage(commandSender);
			return true;
		}

		Player player = (Player) commandSender;

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("#")) {
				if (!sendPermissionError(player, "") && !sendPermissionError(player, "*") && !sendPermissionError(player, "admin"))
					return false;
				
				if (fakeManager.getFakes().containsKey(player.getUniqueId())) {
					randomNames.add(fakeManager.getFakes().getSubValue(player.getUniqueId()));
					fakeManager.applyFake(player, fakeManager.getFakes().getValue(player.getUniqueId()), fakeManager.getFakes().getValue(player.getUniqueId()), false, true);
					fakeManager.removeFake(player.getUniqueId());
				} else {
					player.sendMessage("§cVocê não está usando nenhum nick fake!");
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!sendPermissionError(player, "list") && !sendPermissionError(player, "admin"))
					return false;
				
				if (fakeManager.getFakes().isEmpty()) {
					player.sendMessage("§cNão há ninguém utilizando um fake no momento.");
					return false;
				}
				
				for (Object uuid : fakeManager.getFakes().keyToArray()) {
					Player toShow = Bukkit.getPlayer((UUID) uuid);
					player.sendMessage("§7 §a" + toShow.getName() + " §7| " + fakeManager.getFakes().getValue(toShow.getUniqueId()));
				}
			} else if (args[0].equalsIgnoreCase("random")) {
				if (!sendPermissionError(player, "random") && !sendPermissionError(player, "*"))
					return false;
				
				if (randomNames.isEmpty()) {
					player.sendMessage("");
					player.sendMessage("§cHouve um problema ao tentar aplicar o fake.");
					player.sendMessage("§cPossiveis causas:");
					player.sendMessage("§4§l* §eNão há nomes na lista de fakes aleatórios.");
					player.sendMessage("§4§l* §eNão foi possivel carregar os nomes aleatórios do banco de dados.");
					player.sendMessage("");
					return false;
				}

				String randomName = getRandomName();
				randomNames.remove(randomName);
				fakeManager.applyFake(player, randomName, player.getName(), true, false);
				return true;
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (!sendPermissionError(player, "reload") && !sendPermissionError(player, "admin"))
					return false;
				
				reloadRandomNames();
				player.sendMessage("§aNomes aleatórios recarregados com sucesso!");
			} else {
				sendErrorMessage(player);	
			}
		} else if (args.length == 2) {
			if (!sendPermissionError(player, "customname") && !sendPermissionError(player, "*") && !sendPermissionError(player, "admin"))
				return false;
			
			String nick = args[0];

			if (canUse(player, nick)) {
				if (fakeManager.isPremium(nick)) {
					player.sendMessage("§cO nick digitado pertece a uma conta original.");
					return false;
				}

				fakeManager.applyFake(player, nick, args[1], false, false);
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("bypass")) {
				if (!sendPermissionError(player, "customname") && !sendPermissionError(player, "*"))
					return false;
				
				String nick = args[1];

				if (canUse(player, nick)) {
					fakeManager.applyFake(player, nick, args[2], false, false);
				}
			} else if (args[0].equalsIgnoreCase("random")) {
				if (args[1].equalsIgnoreCase("add")) {
					if (!sendPermissionError(player, "random.add") && !sendPermissionError(player, "admin"))
						return false;
					
					if (canUse(player, args[2])) {
						getManager().getMySQLManager().updateFakeNames(args[2]);
						reloadRandomNames();
						player.sendMessage("§aO nome §f" + args[2] + "§a foi adicionado na lista de nomes fakes aleatórios.");
					}
				} else if (args[1].equalsIgnoreCase("remove")) {
					if (!sendPermissionError(player, "random.remove") && !sendPermissionError(player, "admin"))
						return false;
					
					if (getManager().getMySQLManager().getString("fake_names", "names").contains(args[2])) {
						getManager().getMySQLManager().removeFakeName(args[2]);
						reloadRandomNames();
						player.sendMessage("O nome §c" + args[2] + "§7 foi removido da lista de nomes fakes aleatórios.");
					} else {
						player.sendMessage("§cO nick digitado não foi encontrado no banco de dados.");
					}
				} else if (args[1].equalsIgnoreCase("list")) {
					if (args[2].equalsIgnoreCase("all")) {
						if (!sendPermissionError(player, "random.list") && !sendPermissionError(player, "admin"))
							return false;
						
						player.sendMessage("§aNomes fakes registrados no banco de dados:");
						if (!getManager().getMySQLManager().getString("fake_names", "names").isEmpty()) {
							player.sendMessage(getManager().getMySQLManager().getString("fake_names", "names").replace(",", ", "));
						}
					}
				} else {
					sendErrorMessage(player);
				}
			} else {
				sendErrorMessage(player);
			}
		} else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("random")) {
				if (args[1].equalsIgnoreCase("edit")) {
					if (!sendPermissionError(player, "random.edit") && !sendPermissionError(player, "admin"))
						return false;
					
					if (getManager().getMySQLManager().getString("fake_names", "names").contains(args[2])) {
						if (canUse(player, args[3])) {
							if (!getManager().getMySQLManager().getString("fake_names", "names").contains(args[3])) {
								getManager().getMySQLManager().replaceFakeName(args[2], args[3]);
								reloadRandomNames();
								player.sendMessage("§aO nick §f" + args[2] + "§a foi alterado na lista de nomes fakes aleatórios para §f" + args[3] + "§a.");
							} else {
								player.sendMessage("§cO nick §f" + args[2] + "§c já está no registro de nomes fakes, por favor escolha outro nick.");
							}
						}
					} else {
						player.sendMessage("§cO nick digitado não foi encontrado no banco de dados.");
					}				
				} else {
					sendErrorMessage(player);
				}
			} else {
				sendErrorMessage(player);
			}
		} else {
			sendErrorMessage(player);
		}
		
		return false;
	}

	public boolean canUse(Player player, String nick) {
		if (fakeManager.getFakes().containsKey(player.getUniqueId())) {		
			player.sendMessage("");
			player.sendMessage("§cVocê já está utilizando um fake.");
			player.sendMessage("§cDigite §f'/fake #' §cpara voltar a ser §f" + fakeManager.getFakes().getValue(player.getUniqueId()) + "§c.");
			player.sendMessage("");
			return false;
		}
		
		if (!isValid(nick)) {
			player.sendMessage("§cO nick digitado contem caracteres inválidos!");
			return false;
		}

		if (nick.length() > 12) {
			player.sendMessage("§cO nick digitado é muito grande!");
			return false;
		}

		if (nick.length() < 4) {
			player.sendMessage("§cO nick digitado é muito pequeno!");
			return false;
		}

		if (isUsing(nick)) {
			player.sendMessage("§cO nick digitado já está em uso.");
			return false;
		}
		return true;
	}

	public boolean isUsing(String name) {
		for (Object id : fakeManager.getFakes().keyToArray()) {
			String fake = fakeManager.getFakes().getSubValue((UUID) id);

			if (fake.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean isValid(String str) {
		return str.matches("[a-zA-Z0-9]+");
	}

	public String getRandomName() {
		return randomNames.get(new Random().nextInt(randomNames.size() - 1));
	}
	
	public void reloadRandomNames() {
		randomNames.clear();
		
		if (!getManager().getMySQLManager().getString("fake_names", "names").isEmpty()) {
			randomNames.addAll(Arrays.asList(getManager().getMySQLManager().getString("fake_names", "names").split(",")));
			
			for (Object id : fakeManager.getFakes().keyToArray()) {
				String fake = fakeManager.getFakes().getSubValue((UUID) id);

				if (randomNames.contains(fake))
					randomNames.remove(fake);
			}
		}
	}
	
	public boolean sendPermissionError(Player player, String permission) {
		if (!player.hasPermission(PermissionsManager.getFakeCommandPermission(permission))) {
			sendPermissionMessage(player);
			return false;
		}
		return true;
	}
	
	public void sendErrorMessage(Player player) {
		String prefix = "";
		player.sendMessage(prefix + "§cUso correto do comando:");
		
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("random")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("*")))
			player.sendMessage(prefix + "§e/fake random§7 - §fUtilize o nome e skin de um jogador aleatório.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("random.add")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake random add (nome)§7 - §fAdicione um novo nick a lista de nomes aleatórios.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("random.remove")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake random remove (nome)§7 - §fRemova um nick da lista de nomes aleatórios.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("random.edit")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake random edit (nome antigo) (novo nome)§7 - §fEdite nomes aleaórios que estão registrados.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("random.list")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake random list all§7 - §fVeja todos os nicks aleatórios registrados.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("customname")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("*")))
			player.sendMessage(prefix + "§e/fake (nome) (skin)§7 - §fUtilize o nome e skin de um outro jogador.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("bypass")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("*")))
			player.sendMessage(prefix + "§e/fake bypass (nome) (skin)§7 - §fUtilize o nome e skin de um outro jogador.");
		if (player.hasPermission(PermissionsManager.getCommandPermission("fake")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("*")))
			player.sendMessage(prefix + "§e/fake #§7 - §fVolte ao seu nick e skin padrão.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("list")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake list§7 - §fVeja todos os jogadores que estão utilizando fake.");
		if (player.hasPermission(PermissionsManager.getFakeCommandPermission("reload")) || player.hasPermission(PermissionsManager.getFakeCommandPermission("admin")))
			player.sendMessage(prefix + "§e/fake reload§7 - §fRecarregue os nicks aleatórios.");
	}

}
