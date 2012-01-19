package net.jonnay.infoapi.version;

import org.bukkit.Bukkit;
import seta.infoapi.HttpResponse;
import seta.infoapi.HttpContentResponse;
import seta.infoapi.HttpErrorResponse;
import seta.infoapi.InfoApiEndpoint;

public class Version extends InfoApiEndpoint {
	String endpoint = "/version";
	String docString = "Returns the current version of the server";
	
	public HttpResponse methodGet() {
		return new HttpContentResponse(Bukkit.getServer().getVersion());
	}
}
