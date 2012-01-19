package seta.infoapi;

import java.util.logging.Logger;

/**
 * OK, here is the good mojo.
 * If you want to create your own InfoApi endpoint, it should be easy enough.
 * For every method that is available, just override method<method> and you are
 * good to go.
 */

// TODO Think about syncronized access here.  We might want to move to a
// thredpool model. 
public abstract class InfoApiEndpoint {
	String endpoint;
	String docString;

	Logger log = Logger.getLogger("Minecraft");
	
	public HttpResponse methodOptions() {
		return new HttpContentResponse("/"+endpoint+" -- "+docString);
	}
	
	public HttpResponse methodGet() {
		return methodNotAllowed();
	}
	
	public HttpResponse methodPost() {
		return methodNotAllowed();
	}

	public HttpResponse methodPut() {
		return methodNotAllowed();
	}

	public HttpResponse methodDelete() {
		return methodNotAllowed();
	}

	private HttpResponse methodNotAllowed() {
		return new HttpErrorResponse(405, "Method Not Allowed", "The Endpoint "+endpoint+" doesn't understand this method");
	}
}
