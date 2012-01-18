package seta.infoapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.logging.Logger;

class Server extends Thread {

    protected boolean threadShouldStop = false;
    Logger log = Logger.getLogger("Minecraft");
    ServerSocket serverSocket;

    PrintWriter out;
    Config configuration;

    CommandWorker comWorker;

    public Server(Config cfg) {
	configuration = cfg;
	comWorker = new CommandWorker(cfg);
    }

    public void run() {
	while (!this.isClosing()) {
	    try {

		listenSocket();

	    } catch (Exception e) {
		log.info("[InfoApi] ServerThread had some Problems while running");
		e.printStackTrace();

		this.close();
	    }
	}
    }

    private void listenSocket() {
	// Cast from String to Integer / has Problems with configuration

	Integer serverPort = Integer.valueOf(configuration.getConfig("PORT")).intValue();

	ServerSocket serverSocket = null;

	try {
	    serverSocket = new ServerSocket(serverPort);

	} catch (IOException e) {
	    log.info("[InfoApi] Couldn't listen to given Port: " + Integer.toString(serverPort));
	    this.close();
	}

	while (true) {
	    ClientWorker client;
	    try {
		client = new ClientWorker(serverSocket.accept(), comWorker);
		Thread clientThread = new Thread(client);
		clientThread.start();
	    } catch (IOException e) {
		log.info("[InfoApi] Couldn't accept on: " + Integer.toString(serverPort));
		this.close();
	    }
	}
	// try {
	// clientSocket = serverSocket.accept();
	// } catch (IOException e) {
	// log.info("[InfoApi] couldn't accept on: " +
	// Integer.toString(serverPort));
	// }
    }

    /**
     * Stops the Plugin softly
     */
    public synchronized void close() {
	this.threadShouldStop = true;

	try {
	    serverSocket.close();
	} catch (Exception e) {
	    log.info("[InfoApi] ServerThread a Problem while closing");
	}
    }

    protected synchronized boolean isClosing() {
	return this.threadShouldStop;
    }
}