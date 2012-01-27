package seta.infoapi;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandWorker {
	
    private FileConfiguration configuration;
    Logger log = Logger.getLogger("Minecraft");
	InfoApi plugin;
	
    public CommandWorker(InfoApi plugin, FileConfiguration cfg) {
		configuration = cfg;
		this.plugin = plugin;
    }

	private String getMethod(String requestLine) {
		return requestLine.substring(0, requestLine.indexOf(" "));
	}

	private URL getUrl(String requestLine) throws java.net.MalformedURLException  {
		return new URL("http://localhost"+
					   requestLine.substring((requestLine.indexOf(" ")+1),
											 requestLine.lastIndexOf(" HTTP/")));
	}
	
    /*
	 * Soon this will eat and enjoy the entire god-damned header. Just the request line for now tho.
     */
    public HttpResponse processCommand(String requestLine) {
		try {
			String command;
			URL url;
			String method;

			method = getMethod(requestLine);
			try {
				url = getUrl(requestLine);
			} catch (java.net.MalformedURLException e) {
				log.severe("[InfoApi] Malformed URL! This should never have happened!  Request line was: "+requestLine);
				log.severe(e.toString());
				// TODO maybe this should be a 400 Bad Request
				return new HttpErrorResponse(500, "Internal Server Error", e.toString());
			}

			
			if (configuration.getBoolean("log-requests")) {
				log.info("[InfoApi] ("+method+") ("+url.toString()+")");
			}


			if (!authenticates(url)) {
				return new HttpErrorResponse(403, "Unauthorized", "Please use a valid secret token");				
			}

			/* Right now endpoints are basically mounted on the root */
			
			String[] pathFrags = url.getPath().substring(1).split("/");  
			command = "/"+pathFrags[0];
				
			log.info("[InfoApi] running: ("+command+")");

			InfoApiEndpoint e = plugin.getEndpointManager().getEndpoint(command);
			if (e != null) {
				return e.handle(method, url, pathFrags);
			} else {
				return new HttpErrorResponse(404, "Not Found", "The endpoint "+command+" is not available.");
			}
			
			// catching Exception e?  Really? This needs to die soon.
		} catch (Exception e) {
			log.severe("[InfoApi] Exception while calling method on endpoint " + e.toString());
			e.printStackTrace();
			return new HttpExceptionResponse(e);
		}
    }

	// Right now this is dumb, it thinks that there is only going to ever be 1 query on the URL.
	//TODO enable user/password authentication on URL
	//TODO enable hashed passwords 
	public boolean authenticates(URL url) {
		String secretkey = configuration.getString("secretKey");
		String query = url.getQuery();
		
		// every time we check this, we want to be LOUD about the fact the secret is unsecure
		if (secretkey.equals("secret")) {
			log.severe("[InfoApi] SECRET KEY IS INSECURE! (edit plugins/InfoApi/config.yml and Choose something sekrut!)");
		}

		log.info("[InfoApi] Comparing query:"+query+" to:secret="+secretkey);
		
		return ((query != null) && (query.equals("secret="+secretkey)));
	}
}
