package seta.infoapi;

public class InfoApiEndpointLoaderException extends Exception {
	private String msg;

	public InfoApiEndpointLoaderException(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
}
