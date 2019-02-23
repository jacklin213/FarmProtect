package me.jacklin213.farmprotect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.jacklin213.farmprotect.utils.Updater;
import me.jacklin213.farmprotect.utils.Updater.UpdateResult;
import me.jacklin213.farmprotect.utils.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FarmProtect extends JavaPlugin implements Listener {
	
	public static FarmProtect plugin;

	public Logger log;
	public Updater updater;
	public String chatPluginName = ChatColor.translateAlternateColorCodes('&', "&c[&6FarmProtect&c] ");
	List<String> worlds = new ArrayList<>();
	
	public void onEnable() {
		this.setLogger();
		this.createConfig();
		
		this.getConfig().getStringList("Worlds").stream()
				.filter(world -> Bukkit.getWorld(world) != null)
				.forEach(world -> worlds.add(world));
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));
		Boolean autoUpdate = Boolean.valueOf(getConfig().getBoolean("AutoUpdate"));
		
		this.updateCheck(updateCheck, autoUpdate, 44691);
		log.info(String.format("Version %s by jacklin213 has been Enabled!", getDescription().getVersion()));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
			sendHelp(sender);
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("farmprotect.reload")) {
					this.reloadConfig();
					sender.sendMessage(chatPluginName + ChatColor.GREEN + "plugin has successfully reloaded.");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have the permissions to use this command!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("toggle")) {
				if (sender.hasPermission("farmprotect.multiworld.toggle")) {
					if (this.getConfig().getBoolean("MultiWorld")) {
						this.getConfig().set("MultiWorld", false);
						sender.sendMessage(chatPluginName + ChatColor.RED + "MultiWorld has been turned off.");
					} else {
						this.getConfig().set("MultiWorld", true);
						sender.sendMessage(chatPluginName + ChatColor.GREEN + "MultiWorld has been turned on.");
					}
					try {
						this.getConfig().save(new File(getDataFolder(), "config.yml"));
					} catch (IOException e) {
						e.printStackTrace();
					} 
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have the permissions to use this command!");
					return true;
				}
			} else {
				sendHelp(sender);
			}
		} else if (args.length > 1) {
			if (args[0].equalsIgnoreCase("add")) {
				if (sender.hasPermission("farmprotect.addworld")) {
					if (Bukkit.getWorld(args[1]) == null) {
						sender.sendMessage(chatPluginName + ChatColor.RED + "not a valid world.");
					} else {
						if (worlds.contains(args[1].toLowerCase())) {
							sender.sendMessage(chatPluginName + ChatColor.RED + "This world is already protected!");
						} else {
							worlds.add(args[1]);
							this.getConfig().set("Worlds", worlds);
							try {
								this.getConfig().save(new File(getDataFolder(), "config.yml"));
							} catch (IOException e) {
								e.printStackTrace();
							} 
							sender.sendMessage(chatPluginName + ChatColor.GOLD + args[1] + ChatColor.GREEN + "has been added.");
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have the permissions to use this command!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission("farmprotect.removeworld")) {
					if (!worlds.contains(args[1].toLowerCase())) {
						sender.sendMessage(chatPluginName + ChatColor.RED + "This world isn't being protected!");
					} else {
						worlds.remove(args[1]);
						this.getConfig().set("Worlds", worlds);
						try {
							this.getConfig().save(new File(getDataFolder(), "config.yml"));
						} catch (IOException e) {
							e.printStackTrace();
						} 
						sender.sendMessage(chatPluginName + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " has been removed.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have the permissions to use this command!");
					return true;
				}
			} else {
				sendHelp(sender);
			}
		} 
		return false;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(formatColor("&6o0=======&c[&eFarmProtect&c]&6========0o"));
		sender.sendMessage(formatColor("&bNOTE &f: &e" + "All farmprotect commands have an alias of /fp"));
		sender.sendMessage(formatColor("&b/fp toggle &f- &e" + "Turns on multiworld protection support"));
    	sender.sendMessage(formatColor("&b/fp add <world> &f- &e" + "Adds a world into protection"));
    	sender.sendMessage(formatColor("&b/fp remove <world> &f- &e" + "Removes protection for a world"));
    	sender.sendMessage(formatColor("&b/fp reload &f- &e" + "Reloads the config"));
	}
	
	public String formatColor(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)) {
			if (this.getConfig().getBoolean("MultiWorld")) {
				if (worlds.contains(event.getClickedBlock().getWorld().getName().toLowerCase())) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event)	{
		if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) {
			if (this.getConfig().getBoolean("MultiWorld")) {
				if (worlds.contains(event.getBlock().getWorld().getName().toLowerCase())) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	private void createConfig() {
		// Creates config.yml
		File file = new File(getDataFolder() + File.separator + "config.yml");
		// If config.yml doesnt exit
		if (!file.exists()) {
			// Tells console its creating a config.yml
			log.info("Cannot find config.yml, Generating now....");
			this.saveDefaultConfig();
			log.info("Config generated !");
		}

	}
	
	private void setLogger() {
		log = getLogger();
	}
	
	private void updateCheck(boolean updateCheck, boolean autoUpdate, int ID) {
		if(updateCheck && (autoUpdate == false)) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE) {
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			}
		}
		if(autoUpdate && (updateCheck == false)) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_VERSION_CHECK, true);
		} 
		if(autoUpdate && updateCheck) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.DEFAULT, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE) {
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			}
		}
	}
	
}
