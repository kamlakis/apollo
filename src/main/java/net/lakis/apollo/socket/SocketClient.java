package net.lakis.apollo.socket;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.etsy.net.JUDS;
import com.etsy.net.UnixDomainSocketClient;

public class SocketClient implements Closeable {
	private UnixDomainSocketClient unixDomainSocketClient;
	private Socket socket;
	private SocketType socketType;
	private PrintWriter out;
	private BufferedReader in;

	public SocketClient(String path) throws IOException {
		this.unixDomainSocketClient = new UnixDomainSocketClient(path, JUDS.SOCK_STREAM);
		this.socketType = SocketType.UNIX;
	}

	public SocketClient(String host, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(host, port);
		this.socketType = SocketType.TCP;

	}

	public PrintWriter getOutput() throws IOException {
		if (out == null) {
			switch (this.socketType) {
			case TCP:
				out = new PrintWriter(socket.getOutputStream(), true);
				break;

			case UNIX:
				out = new PrintWriter(unixDomainSocketClient.getOutputStream(), true);
				break;

			}
		}
		return out;
	}

	public BufferedReader getInput() throws IOException {
		if (in == null) {
			switch (this.socketType) {
			case TCP:
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				break;

			case UNIX:
				in = new BufferedReader(new InputStreamReader(unixDomainSocketClient.getInputStream()));
				break;

			}
		}
		return in;
	}

	public void close() {
		try {
			switch (this.socketType) {
			case TCP:
				socket.close();
			case UNIX:
				unixDomainSocketClient.close();
			}
		} catch (Exception e) {
		}

		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
		}
		
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
		}
	}

}
