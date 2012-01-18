package seta.infoapi;

import java.util.logging.Logger;
import java.io.UnsupportedEncodingException;

public abstract class HttpResponse {

	static final String NL = "\r\n";
	
    static Logger log = Logger.getLogger("Minecraft");
	
	int statusCode;
	String statusMessage;

	String content;
	String contentLength;
	
	public void setContent(String c) {
		content = c;
		try {
			contentLength = Integer.toString(content.getBytes("UTF8").length);
		} catch (UnsupportedEncodingException e) {
			log.info("InfoApi had some Problems while getting Bytesize of String");
	    }
	}
	
	public String toString() {
		String out = "";
		
		// its been to long, is this cast necessary?
		out += "HTTP/1.1 "+Integer.toString(statusCode)+" "+statusMessage+NL;
		out += "Content-Language: en"+NL;
		out += "Content-Type: text/plain"+NL;
		out += "Content-Length: "+contentLength+NL;

		out += NL;
		out += content;
		
		return out;		
	}
}
