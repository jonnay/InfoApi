package net.jonnay.infoapi.status;

import org.bukkit.Bukkit;
import seta.infoapi.HttpResponse;
import seta.infoapi.HttpContentResponse;
import seta.infoapi.InfoApiEndpoint;

public class OnlineMode extends InfoApiEndpoint {
	String endpoint = "/onlineMode";
	String docString = "Returns the current version of the server";
	
	public HttpResponse methodGet() {
		return new HttpContentResponse(Boolean.toString(Bukkit.getServer().getOnlineMode()));
	}
}
