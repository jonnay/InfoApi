package seta.infoapi;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class InfoApi extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");
    Server server;

    @Override
    public void onEnable() {

	Config configuration = new Config();
	server = new Server(configuration);

	server.start();

	// resurrect server thread if it crashes
	if (!server.isAlive()) {
	    server.start();
	    log.info("[InfoApi] Server was resurrected");
	}

	log.info("[InfoApi] Enabled");
    }

    @Override
    public void onDisable() {
	server.close();
	log.info("[InfoApi] Disabled");
    }

}