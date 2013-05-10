package me.jacklin213.farmprotect;

import java.io.File;
import java.util.logging.Logger;

import me.jacklin213.farmprotect.utils.UpdateChecker;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FarmProtect extends JavaPlugin implements Listener{
	
	public static FarmProtect plugin;

	public Logger log = Logger.getLogger("Minecraft");
	public UpdateChecker updateChecker;
		
	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription()
				.getName(), getDescription().getVersion()));
	}
	
	public void onEnable() {
		
		createConfig();
		
		Boolean updateCheck = getConfig().getBoolean("updatecheck");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		this.updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/farmprotect/files.rss");
		
		if ((updateCheck) && (this.updateChecker.updateNeeded())) {
			this.log.info(String.format("[%s] A new update is avalible, Version: %s", getDescription().getName(), this.updateChecker.getVersion()));
			this.log.info(String.format("[%s] Get it now from: %s", getDescription().getName(), this.updateChecker.getLink()));
		}
		this.log.info(String.format("[%s] Enabled Version %s by jacklin213", getDescription()
				.getName(), getDescription().getVersion()));

	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
		if (commandLabel.equalsIgnoreCase("farmprotect")){
			if (sender.hasPermission("farmprotect.reload")){
				if (args[0].equalsIgnoreCase("reload")){
					this.reloadConfig();
					return true;
				}
			} else {
				sender.sendMessage("You do not have the permissions to use this command!");
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void noFarmlanddestroy(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
			event.setCancelled(true);
	}
	
	public void createConfig() {
		// Creates config.yml
		File file = new File(getDataFolder() + File.separator + "config.yml");
		// If config.yml doesnt exit
		if (!file.exists()) {
			// Tells console its creating a config.yml
			this.getLogger().info(String.format("[%s] Cannot find config.yml, Generating now....", getDescription().getName()));
			this.getLogger().info(String.format("[%s] Config generated !", getDescription().getName()));
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
		}

	}


}
