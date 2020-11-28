package net.lakis.apollo.socket;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketServer;

public class SocketServer {
	private SocketType socketType;
	private ServerSocket serverSocket;
	private UnixDomainSocketServer unixDomainSocketServer;

	public SocketServer(String host, int port) throws IOException {

		this.serverSocket = new ServerSocket();
		this.serverSocket.setReuseAddress(true);

		this.serverSocket.bind(new InetSocketAddress(host, port));
		this.socketType = SocketType.TCP;

	}

	public SocketServer(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		this.unixDomainSocketServer = new UnixDomainSocketServer(path, JUDS.SOCK_STREAM, 10);
		this.socketType = SocketType.UNIX;
	}

	public Socket accept() throws IOException {
		switch (this.socketType) {
		case TCP:
			return new Socket(this.serverSocket.accept());

		case UNIX:
			return new Socket(this.unixDomainSocketServer.accept());
		}
		return null;
	}

	public void close() throws IOException {
		switch (this.socketType) {
		case TCP:
			this.serverSocket.close();
			break;
		case UNIX:
			this.unixDomainSocketServer.close();
			break;

		}
	}

}
