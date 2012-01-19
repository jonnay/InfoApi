package seta.infoapi;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class InfoApi extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");
    Server server;

    @Override
    public void onEnable() {

		//Config configuration = new Config();

		server = new Server(getConfig());  // not sure I like passing aorund the config so much, but...
		this.saveConfig();  // K, this is kinda lame, I 'd rather test to see if it exists before just saving it.. but..
		
		server.start();

		// resurrect server thread if it crashes
		if (!server.isAlive()) {
			server.start();
			log.warning("[InfoApi] Server was resurrected");
		}

		log.info("[InfoApi] (bunny version) Enabled");
    }

    @Override
    public void onDisable() {
		server.close();
		log.info("[InfoApi] Disabled");
    }

}
