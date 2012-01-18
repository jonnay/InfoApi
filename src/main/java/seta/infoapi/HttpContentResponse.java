package seta.infoapi;

public class HttpContentResponse extends HttpResponse {
	public HttpContentResponse(String output) {
		this.statusCode = 200;
		this.statusMessage = "OK";
		this.setContent(output);
	}
}
