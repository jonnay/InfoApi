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
import java.net.InetAddress;
import java.util.Vector;

/**
 * honestly, the sub-endpoints could be an array, but that might be going too far.
 */
public class Player extends InfoApiEndpoint {
	String endpoint = "/player";
	String subEndpoints = "online, count, count/<world>, world/<world>, info/<name>";
	String docString = "Access to players on the server.  Possible sub-endpoints are: "+subEndpoints;
	
	InfoApi plugin;
	
	public Player(InfoApi p) {
		plugin = p;
	}

	
	public HttpResponse getMethod(EndpointState s) {
		/* NOTE:
		 * This whole mess means that the subendpoint thing needs to happen SOOON..
		 */
		   
		String sub;
		
		String[] path = s.getPathFrags();
		if (path.length < 2)
			return subEndpointNotFound("Empty sub-endpoint");
		
		sub = path[1];

		try {
		
			if (sub.equals("info")) {
				if (path.length < 3 ) {              // I love you sunnay
					return new HttpErrorResponse(404, "Not Found", "No player Specified!");
				} else {
					return getPlayer(path[2], s);
				}

			} else if (sub.equals("world")) {
				if (path.length < 3) {
					return new HttpErrorResponse(404, "Not Found", "No world Specified!");
				} else {
					return getPlayersInWorld(path[2], s);
				}

			} else if (sub.equals("online")) {
				return getOnlinePlayers(s);

			} else if (sub.equals("count")) {
				if (path.length < 3) {
					return getNumberOfPlayers(s);
				} else {
					return getNumberOfPlayersInWorld(path[2], s);
				}
			}
			/*else if (sub.equals("offline")) {
			  return getOfflinePlayers(s);
			  }*/
			else {
				return subEndpointNotFound("Sub-endpoint "+sub+" doesn't exist");
			}
		} catch (java.net.UnknownHostException e) {
			// okay, this should never happen, but if it does, we want to know about it.
			log.severe("[InfoApi] "+e.toString());
			e.printStackTrace();
			return new HttpExceptionResponse(e);
			
		}
	}


	
	/*
	  Offline player
	  - getName
	  - isOnline
	  - isOp
	  - isWhitelisted
	  - getFirstPlayed
	  - getLastPlayed
	*/

	/*
	  getOnlinePlayers 
	  get
	  s - getDisplayName
	  i - getExp
	  i - getLevel
	  i - getHealth
	  i - getTicksLived
	  post
	  - damage(amount)
	  - kick(msg)
	  - sendMessage(msg)
	*/
	
	HttpResponse getPlayer(String name, EndpointState s) {
		org.bukkit.entity.Player p = Bukkit.getServer().getPlayerExact(name);

		if (p == null) {
			return new HttpErrorResponse(404, "Not Found", "Player "+name+" was not found.  Player names are CaSe-SENsatIVe.");
		} else {
			String baseData = "";
			String onlineData = "";

			baseData = p.getName() + "\n" +
				(p.isOnline() ? "Online" : "Offline") + "\n" +
				(p.isOp() ? "Op" : "Player") + "\n";
			// Next version of bukkit?
			//p.getFirstPlayed() + "\n" +
			//	p.getLastPlayed() + "\n";

			if (p.isOnline()) {
				int level = p.getLevel();
				//  Not ready yet.  next version of bukkit?				int exp = p.getExp();
				onlineData = p.getDisplayName() + "\n" +
					p.getHealth() + "\n" +
					level + "\n" +
					p.getTicksLived() + "\n" ;
				
			}

			return new HttpContentResponse(baseData+"\n"+onlineData);
		}
	}

	HttpResponse getNumberOfPlayers(EndpointState s)
		throws java.net.UnknownHostException
	{
		return new HttpContentResponse(Integer.toString(filterPlayers(Bukkit.getServer().getOnlinePlayers()).length));
	}

	HttpResponse getNumberOfPlayersInWorld(String worldName, EndpointState s)
		throws java.net.UnknownHostException
	{
		HttpResponse r = assertWorld(worldName);
		if (r != null)
			return r;

		return new HttpContentResponse(Integer.toString(filterPlayers(Bukkit.getServer().getWorld(worldName).getPlayers()).length));
	}
	
	HttpResponse getOnlinePlayers(EndpointState s)
		throws java.net.UnknownHostException
	{
	    return playerArrayAsResponse(filterPlayers(Bukkit.getServer().getOnlinePlayers()));
	}

	HttpResponse getPlayersInWorld(String worldName, EndpointState s)
		throws java.net.UnknownHostException
	{
		HttpResponse r = assertWorld(worldName);
		if (r != null)
			return r;
		
		return playerArrayAsResponse(filterPlayers(Bukkit.getServer().getWorld(worldName).getPlayers()));
	}
	
	/**
	 * not ready yet, next version of bukkit?
	 *
	HttpResponse getOfflinePlayers(EndpointState s) {
		org.bukkit.entity.Player[] pl = Bukkit.getServer().getOfflinePlayers(); 
		return playerArrayAsResponse(pl);
	}
	*/


	// returns null if the world exists, otherwise an HttpErrorResponse
	// Used to check to see if a world exists
	private HttpErrorResponse assertWorld(String worldName) {
		if (null == Bukkit.getServer().getWorld(worldName))
			return new HttpErrorResponse(404, "Not Found", "That world does not exist.");

		return null;
	}

	// There must be a better way to do this.. but....
	private org.bukkit.entity.Player[] filterPlayers(java.util.List<org.bukkit.entity.Player> players)
		throws java.net.UnknownHostException
	{
		return filterPlayers(players.toArray(new org.bukkit.entity.Player[0]));
	}
	
	// <rant> Really? this rigamarole to filter an array?  Maybe it is time to look into google collections
	// This code makes me feel kinda squicky.. and maybe it can be improved.
									 
	private org.bukkit.entity.Player[] filterPlayers(org.bukkit.entity.Player[] players)
		throws java.net.UnknownHostException
	{
		// if we're not running in npcSAFEmode...
		if (!plugin.getConfig().getBoolean("npcSaveMode"))
			return players;
		
		Vector<org.bukkit.entity.Player> newPlayers = new Vector<org.bukkit.entity.Player>(players.length);

		String localHostName = InetAddress.getLocalHost().getHostName();
		
		for (org.bukkit.entity.Player player: players) {
			if (player.isOnline()) {
				String playerHostName = player.getAddress().getHostName();
				if (!localHostName.equals(playerHostName)) {
					newPlayers.addElement(player);
				}
			}
		}

		//Array<org.bukkit.entity.Player> out = new Array<org.bukkit.entity.Player>(newPlayers.size());
		//newPlayers.copyInto(out);
		//return out;
		return newPlayers.toArray(new org.bukkit.entity.Player[0]);
	}
	
	private HttpResponse playerArrayAsResponse(org.bukkit.entity.Player[] players) {
		String out = "";

		for (org.bukkit.entity.Player player: players) {
			out = out + player.getName() + " ";
		}
		return new HttpContentResponse(out);
	}
		
	HttpResponse subEndpointNotFound(String err) {
		return new HttpErrorResponse(404, "Not Found", "sub-endpoint not available, the available sub-endpoints are:"+subEndpoints+"  "+err);
	}


}
