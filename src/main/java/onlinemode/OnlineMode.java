package seta.infoapi;

public class OnlineMode extends InfoApiEndpoint {
	String endpoint = "/onlineMode";
	String docString = "Returns the current version of the server";
	
	public HttpResponse methodGet() {
		return new HttpContentResponse(Boolean.toString(Bukkit.getServer().getOnlineMode());
	}
}
