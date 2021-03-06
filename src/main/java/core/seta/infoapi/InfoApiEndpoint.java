package seta.infoapi;

import java.util.logging.Logger;
import java.net.URL;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
/**
 * OK, here is the good mojo.
 * If you want to create your own InfoApi endpoint, it should be easy enough.
 * For every method that is available, just override <lowercase-method>Method and you are
 * good to go.  I.e. getMethod, postMethod, putMethod, propgetMethod, etc.
 * The methods don't need to be strict HTTP1/1 either, webdav could work.
 */

// TODO Think about syncronized access here.  We might want to move to a
// thredpool model. 
public abstract class InfoApiEndpoint {
	String endpoint;
	String docString;

	protected Logger log = Logger.getLogger("Minecraft");

	/**
	 * You could over-ride handle if you wanted to handle some non-standard HTTP verbs.
	 */
	public HttpResponse handle(String method, URL url, String[] pathFrags)
	{
		String cmethod = method.toLowerCase()+"Method";
		log.info("Calling "+cmethod+" on "+this.endpoint);
	    EndpointState state = new EndpointState(method, url, pathFrags);
		try {
			Class<?> c = this.getClass();
			Method handler = c.getMethod(cmethod, Class.forName("seta.infoapi.EndpointState"));
			return (HttpResponse) handler.invoke(this, state);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return new HttpExceptionResponse(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return new HttpExceptionResponse(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return new HttpExceptionResponse(e);
		} catch (NoSuchMethodException e) {
			return notAllowed(e);
		}
	}

	// TODO Make this do a recursive lookup on itself, and its parents, so it can provide which methods are allowed.
	public HttpResponse methodOptions() {
		return new HttpContentResponse(endpoint+" -- "+docString);
	}

	private HttpResponse notAllowed(Exception e) {
		// we kinda break HTTP here.  Which is sad.
		return new HttpErrorResponse(405, "Method Not Allowed", "The Endpoint doesn't understand this method.  Exception: "+e.getMessage()+"  "+HttpExceptionResponse.getStackTrace(e));
	}
}
