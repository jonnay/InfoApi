package seta.infoapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientWorker implements Runnable {

    private Socket client;
    private CommandWorker comWorker;
    protected boolean threadShouldStop = false;
    Logger log = Logger.getLogger("Minecraft");

    ClientWorker(Socket client, CommandWorker comWorker) {
		this.client = client;
		this.comWorker = comWorker;
    }

    @Override
    public void run() {
		while (!this.isClosing()) {
			try {

				listenSocket();

			} catch (Exception e) {
				log.info("[InfoApi] ClientThread had some Problems while running");
				e.printStackTrace();
				this.close();
			}
		}
    }

    private void listenSocket() {
		BufferedReader input = null;
		PrintWriter output = null;
		String outputString, checkString;

		try {
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			log.info("[InfoApi] Could not start Reading or Writing Handlers");
			this.close();
		}

		while (true) {
			try {
				checkString = input.readLine();
				if (checkString == null || checkString.isEmpty()) {
					output.println(new HttpErrorResponse(400, "Bad Request", "Empty request! o.o"));
					output.flush();
								   
				} if (comWorker.isValidCommandString(checkString)) {
					/*
					  outputString = comWorker.processCommand(checkString);
					  outputString = HTTPWorker.addHTTPHeader(outputString);
			
					  output.println(outputString);
					  output.flush();
					*/
					output.println(comWorker.processCommand(checkString));
					output.flush();
					this.close();
				} else {
					output.println(new HttpErrorResponse(403, "Unauthorized", "Not a valid secret key"));
					output.flush();
					this.close();
				}
			} catch (IOException e) {
				log.info("[InfoApi] Could not read or write");
				log.info("[InfoApi] "+e.toString());
				e.printStackTrace();
				this.close();
			}
		}
    }

    /**
     * Stops the Plugin softly
     */
    public synchronized void close() {
		this.threadShouldStop = true;

		try {
			client.close();
		} catch (Exception e) {
			log.info("[InfoApi] ClientThread a Problem while closing");
		}
    }

    protected synchronized boolean isClosing() {
		return this.threadShouldStop;
    }
}
