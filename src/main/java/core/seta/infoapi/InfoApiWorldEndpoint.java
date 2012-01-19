package seta.infoapi;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;


/**
 * OK, here is the good mojo.
 * If you want to create your own InfoApi endpoint, it should be easy enough.
 * For every method that is available, just override method<method> and you are
 * good to go.
 */

public abstract class InfoApiWorldEndpoint extends InfoApiEndpoint {
	Boolean isValidWorldName(String worldName) {
		try {
			List<World> availableWorlds = Bukkit.getServer().getWorlds();

			for (World wrld : availableWorlds) {
				if (wrld.getName().equals(worldName)) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
		   log.info("isValidWorldName " + e.getMessage());
			return false;
		}
    }
}
