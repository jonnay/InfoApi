package seta.infoapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

class Server extends Thread {

    protected boolean threadShouldStop = false;
    Logger log = Logger.getLogger("Minecraft");
    ServerSocket serverSocket;

    PrintWriter out;

    CommandWorker comWorker;
	FileConfiguration config ;

	InfoApi plugin;
	
    public Server(InfoApi plugin, FileConfiguration config) {
		this.config = config;
		this.plugin = plugin;

		comWorker = new CommandWorker(plugin, config);

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


	/*
	 * Two threads probably makes sense (main thread and server thread)
	 * But creating a thread for each client is probably not the right
	 * thing to do.
	 * Even if it is, I have my doubts about this implementation. 
	 *
	 * This example seems a little more solid 
	 * http://java.sun.com/developer/technicalArticles/Networking/Webserver/WebServercode.html
	 */
    private void listenSocket() {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(config.getInt("port"));
			while (true) {
				handleConnection(serverSocket.accept(), comWorker);
			}
		} catch (IOException e) {
			log.info("[InfoApi] Couldn't listen to given Port: " + Integer.toString(config.getInt("port")));
			this.close();
		}


			// Sockets will likely get eaten up by ClientWorker.
			/*
			ClientWorker client;
			try {
				client = new ClientWorker(serverSocket.accept(), comWorker);
				Thread clientThread = new Thread(client);
				clientThread.start();
			} catch (IOException e) {
				log.info("[InfoApi] Couldn't accept on: " + Integer.toString(serverPort));
				this.close();
			}*/

			// For now do the same thing as ClientWorker, but single threaded
			// and clean up after itsef

		
		
			
	   
		// try {
		// clientSocket = serverSocket.accept();
		// } catch (IOException e) {
		// log.info("[InfoApi] couldn't accept on: " +
		// Integer.toString(serverPort));
		// }
    }

	// syncronized... maybe?
	// wholesale rape and paste of ClientWorker.java... with closing!
	// Exception handling here is BULL IN CHINA SHOP style and needs fixing.
	private void handleConnection(Socket client, CommandWorker comWorker) throws java.io.IOException {
		BufferedReader input = null;
		PrintWriter output = null;
		String outputString, checkString;

		try {
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			log.info("[InfoApi] Could not start Reading or Writing Handlers");
			client.close();
		}

		// except we don't try to handle a single connection forever... just once!
		try {
			String requestLine = input.readLine();

			// Here is where we should parse the header.  Later
			checkString = requestLine;
			
			if (checkString == null || checkString.isEmpty()) {
				output.println(new HttpErrorResponse(400, "Bad Request", "Empty request! o.o"));
				output.flush();
				
			}

			output.println(comWorker.processCommand(checkString).toString());  //toString is implicit here I think, but explicit is a bit easier to understand.
			output.flush();
			client.close();
			
		} catch (IOException e) {
			log.info("[InfoApi] Could not read or write");
			log.info("[InfoApi] "+e.toString());
			e.printStackTrace();
			client.close();
		}
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
