package br.com.raphaelfury.disguise.fake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import br.com.raphaelfury.disguise.manager.Manager;
import br.com.raphaelfury.disguise.mysql.MySQLManager;
import br.com.raphaelfury.disguise.reflection.ReflectionUtils;
import br.com.raphaelfury.disguise.storage.Storage;
import br.com.raphaelfury.disguise.tag.TagAPI;
import br.com.raphaelfury.disguise.tag.TagManager;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class FakeManager {

	private Manager manager;
	private MySQLManager mysqlManager;
	private Storage<UUID, String, String> fakes = new Storage<>();
	private String defaultValue = "eyJ0aW1lc3RhbXAiOjE0NjQyOTYyOTU0MzksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2ZWVjMWMyMTY5YzhjNjBhN2FlNDM2YWJjZDJkYzU0MTdkNTZmOGFkZWY4NGYxMTM0M2RjMTE4OGZlMTM4In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNzY3ZDQ4MzI1ZWE1MzI0NTYxNDA2YjhjODJhYmJkNGUyNzU1ZjExMTUzY2Q4NWFiMDU0NWNjMiJ9fX0=";
	private String defaultSignature = "TOyYc+LBQ5wyGVqXaDvTFejzVOC+ZnsqnXSi9PP4MmCSeU7h0DG6ZwrQbJD3S76wfd+hdIOJurhXW4d/vrDbP4AMUaZzpPRupEZicFxFAl1ZtdtFwzeLYX7COYSLF5nrUy1MSwAN40TnxaEQroLsYFjH0jsqtYLxP1s0WiyNjrEjJ9gWwEPY0fdlNNKjCECYg8vqMafnwsegVifUN8mPJWHykgfGf0sa80nKVTEaNApbbTEHM+EGoU3MDkce65O7tKtSTy979zoKXK+XaJtCQvK5C3s1K49jnxJRHfXcQDW7t6K0VKAoTa5sw/JK4+WmPRNv5eRwOJmGhEcAs1+PN25JB5n+4X/kK2P2eyesIc4DhCUrle+sMifFtaxV6QA15z622wR2XzkUrfiyQyG1b4IuZjuEQcMO/u+rT0PT/Mn5PnofUDagSt/zni+lDT/c8ItXCp1h3oAcMmZ0l4rArIXTeeu6RgRepdrOvKJNr7LjdoHJR9iVCL42GAuEUnwujySKkGP7WfyES9+au7ujPBQhMauMiLFJwoN5RQ9yhv4n2TGwFQ2YArhD4eihDcZ5r/UbpkP9eOS3+C8XZNAK7emrhzob4zFfFTjUBAHxZ92ku9o7Y+PEQN+xItUu70A2aUHJGnE+DEWpaUN7MJmarVhbMZUxuAXjEZiaOjs45z0=";

	public FakeManager(Manager commonManager) {
		this.manager = commonManager;
		this.mysqlManager = manager.getMySQLManager();
	}

	public Storage<UUID, String, String> getFakes() {
		return fakes;
	}

	@SuppressWarnings("deprecation")
	public void applyFake(Player player, String fakeName, String skinName, boolean defaultSkin, boolean restore) {
		if (!restore)
			fakes.put(player.getUniqueId(), player.getName(), fakeName);

		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

		player.setDisplayName(fakeName);
		player.setPlayerListName(fakeName);

		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(entityPlayer));
			((CraftPlayer) onlinePlayers).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
		}

		ReflectionUtils.setValue("name", entityPlayer.getProfile(), fakeName);

		PropertyMap propertyMap = new PropertyMap();

		if (!defaultSkin && isPremium(skinName)) {
			String value = new String(), signature = new String();
			String uniqueId = manager.getUtils().fromRenato(getUniqueId(skinName)).toString();

			if (mysqlManager.contains("skins_cache", "uuid", uniqueId)) {
				ResultSet resultSet = mysqlManager.getResultSet("skins_cache", "uuid", uniqueId);

				try {
					if (resultSet.next()) {
						value = resultSet.getString("value");
						signature = resultSet.getString("signature");
						
						if (System.currentTimeMillis() > (resultSet.getLong("time") + TimeUnit.HOURS.toMillis(5))) {
							String[] skinProperties = getSkinProperties(skinName);
							value = skinProperties[0];
							signature = skinProperties[1];
							mysqlManager.updateSkin(UUID.fromString(uniqueId), value, signature);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				String[] skinProperties = getSkinProperties(skinName);
				value = skinProperties[0];
				signature = skinProperties[1];
				mysqlManager.updateSkin(UUID.fromString(uniqueId), value, signature);
			}

			propertyMap.put("textures", new Property("textures", value, signature));
		} else {
			propertyMap.put("textures", new Property("textures", defaultValue, defaultSignature));
		}

		propertyMap.put("textures", new Property("textures", "", ""));
		ReflectionUtils.setValue("properties", entityPlayer.getProfile(), propertyMap);

		for (Player players : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) players).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(entityPlayer));
			if (players != player)
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
		}

		MinecraftServer.getServer().getPlayerList().moveToWorld(entityPlayer, 0, false, player.getLocation(), false);
		player.teleport(player.getLocation().add(0, 0.2, 0));

		TagManager.setDisplayName(player, "§f" + player.getName());
		
		if (restore) {
			player.sendMessage("§eVocê retirou seu fake e voltou a ser §f" + player.getName() + ".");
			TagAPI.updateTeamsToPlayer(player);
			TagAPI.setNameTag(player.getName(), "A", "§f", "§f");
			TagAPI.updateTeamsToPlayer(player);
		} else {
			player.sendMessage("");
			player.sendMessage("§aVocÊ se transformou em outro jogador.");
			player.sendMessage("§aNome atual: §f" + fakeName + ".");
			player.sendMessage((defaultSkin ? "§aSkin: §fDefault." : "§aSkin: §fSkin do jogador " + skinName + "."));
			player.sendMessage("");
		}
	}

	public void removeFake(UUID uniqueId) {
		if (fakes.containsKey(uniqueId)) {
			fakes.remove(uniqueId, fakes.getValue(uniqueId), fakes.getSubValue(uniqueId));
		}
	}
	
	/* Minecraft Account Check */
	
	private String[] getSkinProperties(String name) {
		String[] skinProperties = {"", "", ""};
		try {
			URLConnection uc = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + getUniqueId(name) + "?unsigned=false").openConnection();
			uc.setUseCaches(false);
			uc.setDefaultUseCaches(false);
			uc.addRequestProperty("User-Agent", "Mozilla/5.0");
			uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
			uc.addRequestProperty("Pragma", "no-cache");

			@SuppressWarnings("resource")
			JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next())).get("properties");
			for (int i = 0; i < properties.size(); i++) {
				try {
					JSONObject property = (JSONObject) properties.get(i);

					skinProperties[0] = (String) property.get("value");
					skinProperties[1] = property.containsKey("signature") ? (String) property.get("signature") : null;
					skinProperties[2] = (String) property.get("name");
				} catch (Exception e) {
					manager.getUtils().error("There was an error trying to apply the authentication property!", manager.getPlugin().getName());
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return skinProperties;
	}

	private String getURLContent(String url) {
		try {
			InputStream connection = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection));
			String line = null;
			String content = "";
			while ((line = reader.readLine()) != null)
				content += line;
			return content;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public boolean isPremium(String name) {
		return getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name).length() > 0;
	}
	
	private String getUniqueId(String name) {
		JsonObject object = null;
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			URLConnection connection = url.openConnection();
			
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 FireFox/25.0");
			
			Scanner scanner = new Scanner(connection.getInputStream());
			StringBuilder stringBuilder = new StringBuilder();
			
			while (scanner.hasNext()) {
				stringBuilder.append(scanner.nextLine() + " ");
			}
			scanner.close();
			
			JsonParser parser = new JsonParser();
			object = (JsonObject) parser.parse(stringBuilder.toString());
			
		} catch (Exception e) { e.printStackTrace(); }
		return object.get("id").toString().replaceAll("\"", "");
	}

}
