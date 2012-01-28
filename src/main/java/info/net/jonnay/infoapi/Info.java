package net.jonnay.infoapi;

import org.bukkit.Bukkit;
import seta.infoapi.HttpResponse;
import seta.infoapi.HttpContentResponse;
import seta.infoapi.HttpErrorResponse;
import seta.infoapi.HttpExceptionResponse;
import seta.infoapi.InfoApiEndpoint;
import seta.infoapi.EndpointState;
import seta.infoapi.InfoApi;
import org.bukkit.plugin.Plugin;

/**
 * honestly, the sub-endpoints could be an array, but that might be going too far.
 */
public class Info extends InfoApiEndpoint {
	String endpoint = "/info";
	String subEndpoints = "version, ram, onlinemode, maxplayers, plugins";
	String docString = "Returns various bits of information about the server.  Possible sub-endpoints are: "+subEndpoints;
	
	public Info(InfoApi p) {
	}

	
	public HttpResponse getMethod(EndpointState s) {
		/* NOTE:
		 * This whole mess will get refactored into its own more generic subclass later.
		 */
		   
		String sub;
		
		String[] path = s.getPathFrags();
		if (path.length < 2)
			return subEndpointNotFound("Empty sub-endpoint");
		
		sub = path[1];

		// I don't like the version_short and plugin_short endpoints, since they are alternate representaitons of the same resource.
		// Not sure what the solution is yet. 
		
		if (sub.equals("version")) {
			return getVersion();
		} else if (sub.equals("version_short")) {
			return getVersionShort();
		}else if (sub.equals("ram")) {
			return getRam();
		} else if (sub.equals("onlinemode")) {
			return getOnlineMode();
		} else if (sub.equals("maxplayers")) {
			return getMaxPlayers();
		} else if (sub.equals("plugins")) {
			return getPlugins(true);
		} else if (sub.equals("plugins_short")) {
			return getPlugins(false);
		} else {
			return subEndpointNotFound("Sub-endpoint "+sub+" doesn't exist");
		}
	}

	HttpResponse getVersion() {
		return new HttpContentResponse(Bukkit.getServer().getVersion());
	}

	HttpResponse getVersionShort() {
		String outputString = Bukkit.getServer().getVersion();
		return new HttpContentResponse(outputString.substring(outputString.indexOf("(") + 1, outputString.lastIndexOf(")")));
	}
	
	HttpResponse getRam() {
		String returnString = "";

		// Total Memory of Java Runtime in MB
		Double totalMemory = Math.floor((Runtime.getRuntime().totalMemory() / Math.pow(10, 6)));

		// Free Memory of Java Runtime in MB
		Double freeMemory = Math.floor((Runtime.getRuntime().freeMemory() / Math.pow(10, 6)));

		// Maximum Memory of Java Runtime in MB
		Double maxMemory = Math.floor((Runtime.getRuntime().maxMemory() / Math.pow(10, 6)));

		// Returns totalMemory, freeMemory and maxMemory - separated by slash
		returnString = totalMemory.toString() + "/" + freeMemory.toString() + "/" + maxMemory.toString();

		return new HttpContentResponse(returnString);

	}

	HttpResponse getOnlineMode() {
		return new HttpContentResponse(Boolean.toString(Bukkit.getServer().getOnlineMode()));
	}
	
	HttpResponse getMaxPlayers() {
		return new HttpContentResponse(Integer.toString(Bukkit.getServer().getMaxPlayers()));
	}

	/*
	 * <rant> if java was funcitonal, I could just pass in the funciton to run (getName vs getFullName) and be done with it.
	 * But it's not. so here we are.
	 */
	HttpResponse getPlugins(boolean longDisplay) {
		Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
		try {
			String returnString = "";

			if (plugins.length > 0) {
				for (Plugin plugin : plugins) {
					if (longDisplay)
						returnString += plugin.getDescription().getFullName() + "\r\n";
					else
						returnString += plugin.getDescription().getName() + "\r\n";
				}
			} else {
				returnString = "";
			}

			return new HttpContentResponse(returnString);
		} catch (Exception e) {
			log.severe("returnPluginNames " + e.getMessage());
			return new HttpExceptionResponse(e);
		}
	}
	
	HttpResponse subEndpointNotFound(String err) {
		return new HttpErrorResponse(404, "Not Found", "sub-endpoint not available, the available sub-endpoints are:"+subEndpoints+"  "+err);
	}
}
