package net.lakis.apollo.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.lakis.apollo.AbstractApollo;
import net.lakis.apollo.socket.Socket;
import net.lakis.apollo.socket.SocketGenerator;
import net.lakis.apollo.socket.SocketServer;

public class Server implements Runnable {

	private volatile boolean running;
	private ExecutorService threadPool;
	private AbstractApollo service;

	private SocketServer socketServer;

	public Server(AbstractApollo service) {
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new ServerShutdownHookup(service));

	}

	public void run() {
		try {
			Thread.currentThread().setName("ConsoleServer");

			while (running) {
				Socket socket = this.socketServer.accept();
				this.threadPool.execute(new SocketHandler(socket, this.service));
			}
		} catch (Exception e) {
			if (running)
				new Thread(this).start();
		}
	}

	public void start() throws Exception {
		SocketGenerator socketGenerator = new SocketGenerator();
		this.socketServer = socketGenerator.generateServer();

		this.threadPool = Executors.newFixedThreadPool(5, new NamedThreadFactory("ConsoleSocketHandler-"));
		running = true;
		new Thread(this).start();
	}

	public void stop() throws IOException {
		this.socketServer.close();
		running = false;
	}

}