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
