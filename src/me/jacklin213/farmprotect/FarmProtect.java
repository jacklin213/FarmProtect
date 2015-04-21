package me.jacklin213.farmprotect;

import java.io.File;
import java.util.logging.Logger;

import me.jacklin213.farmprotect.utils.Updater;
import me.jacklin213.farmprotect.utils.Updater.UpdateResult;
import me.jacklin213.farmprotect.utils.Updater.UpdateType;

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
		
	public void onEnable() {
		this.setLogger();
		this.createConfig();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));
		Boolean autoUpdate = Boolean.valueOf(getConfig().getBoolean("AutoUpdate"));
		
		this.updateCheck(updateCheck, autoUpdate, 44691);
		log.info(String.format("Version %s by jacklin213 has been Enabled!", getDescription().getVersion()));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if (commandLabel.equalsIgnoreCase("farmprotect")) {
			if (sender.hasPermission("farmprotect.reload")) {
				if (args[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[&6FarmProtect&c]&a Has been reloaded"));
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have the permissions to use this command!");
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event)	{
		if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)){
			event.setCancelled(true);
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
