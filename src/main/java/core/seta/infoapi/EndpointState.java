package seta.infoapi;

import java.util.Hashtable;
import java.net.URL;

public class EndpointState {
	String method;
	URL url;
	String[] pathFrags;

	Hashtable state;

	public EndpointState(String method, URL url, String[] pathFrags) {
		state = new Hashtable();
		this.url = url;
		this.method = method;
		this.pathFrags = pathFrags;
	}

	public String getMethod() {
		return method;
	}

	public URL getUrl() {
		return url;
	}

	public String[] getPathFrags() {
		return pathFrags;
	}
	
	public Object get(String key) {
		return this.state.get(key);
	}

	public void put(String key, Object value) {
		this.state.put(key, value);
	}
}
