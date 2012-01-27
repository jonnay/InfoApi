package net.jonnay.infoapi;

import org.bukkit.Bukkit;
import seta.infoapi.HttpResponse;
import seta.infoapi.HttpContentResponse;
import seta.infoapi.HttpErrorResponse;
import seta.infoapi.InfoApiEndpoint;
import seta.infoapi.EndpointState;
import seta.infoapi.InfoApi;

public class Cmd extends InfoApiEndpoint {
	String endpoint = "/version";
	String docString = "Returns the current version of the server";
	
	public Cmd(InfoApi p) {
	}

	/*
	 * the actuall command needs to be knocked off the url, and put in the request entity where it belongs.
	 * I'd like it to return the result of the command too, but one step at a time.
	 * oh yea, and this substring malarky makes me feel dirty.
	 */
	public HttpResponse postMethod(EndpointState s) {
		String cmd = join(s.getPathFrags(), " ").substring(4);

		log.info("[InfoApi] Running: "+cmd);
		
		return new HttpContentResponse(Boolean.toString(Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd)));
	}

	// the fact that there is no native way to join strings is a disgrace to java.
	// snarfed from: http://stackoverflow.com/questions/1515437/java-function-for-arrays-like-phps-join
	private String join(String[] input, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		for(String value : input)
			{
				sb.append(value);
				sb.append(delimiter);
			}
		int length = sb.length();
		if(length > 0)
			{
				// Remove the extra delimiter
				sb.setLength(length - delimiter.length());
			}
		return sb.toString();
	}
}
