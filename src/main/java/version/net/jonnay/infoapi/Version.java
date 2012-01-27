package net.jonnay.infoapi;

import org.bukkit.Bukkit;
import seta.infoapi.HttpResponse;
import seta.infoapi.HttpContentResponse;
import seta.infoapi.HttpErrorResponse;
import seta.infoapi.InfoApiEndpoint;
import seta.infoapi.EndpointState;
import seta.infoapi.InfoApi;

public class Version extends InfoApiEndpoint {
	String endpoint = "/version";
	String docString = "Returns the current version of the server";
	
	public Version(InfoApi p) {
	}
	
	public HttpResponse getMethod(EndpointState s) {
		return new HttpContentResponse(Bukkit.getServer().getVersion());
	}
}
