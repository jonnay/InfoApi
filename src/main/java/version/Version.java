package seta.infoapi;

public class Version extends InfoApiEndpoint {
	String endpoint = "/version";
	String docString = "Returns the current version of the server";
	
	public HttpResponse methodGet() {
		return new HttpContentResponse(Bukkit.getServer().getVersion());
	}
}
