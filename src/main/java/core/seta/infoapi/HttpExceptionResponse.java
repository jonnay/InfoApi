package seta.infoapi;

import java.io.*;

public class HttpExceptionResponse extends HttpResponse {
	public HttpExceptionResponse(Exception e) {
		this.statusCode = 500;
		this.statusMessage = "Internal Server Error";

		this.setContent(e.getMessage() + "\n"+ getStackTrace(e));		
	}

	public static String getStackTrace(Exception e) {
		Writer result =  new StringWriter();
		PrintWriter pw = new PrintWriter(result);

		e.printStackTrace(pw);
		
		return result.toString();
	}
}
