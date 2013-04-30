package me.jacklin213.farmprotect;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class FarmProtect extends JavaPlugin implements Listener{

	PluginDescriptionFile pdfFile;
	private static final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription()
				.getName(), getDescription().getVersion()));
	}

	public void onEnable() {
		log.info(String.format("[%s] Enabled Version %s by jacklin213", getDescription()
				.getName(), getDescription().getVersion()));

	}
	
	@EventHandler
	public void noFarmlanddestroy(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
			event.setCancelled(true);
	}

}
