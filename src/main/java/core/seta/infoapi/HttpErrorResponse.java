package seta.infoapi;

public class HttpErrorResponse extends HttpResponse {
	public HttpErrorResponse(int code, String statusMsg, String output) {
		this.statusCode = code;
		this.statusMessage = statusMsg;
		this.setContent(output);
	}
}
