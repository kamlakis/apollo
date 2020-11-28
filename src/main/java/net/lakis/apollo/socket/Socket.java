package net.lakis.apollo.socket;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.etsy.net.UnixDomainSocket;

 
public class Socket implements Closeable {
	private SocketType socketType;
	private java.net.Socket socket;
	private UnixDomainSocket unixDomainSocket;
	private PrintWriter out;
	private BufferedReader in;

	public Socket(java.net.Socket socket) {
		this.socket = socket;
		this.socketType = SocketType.TCP;
	}

	public Socket(UnixDomainSocket unixDomainSocket) {
		this.unixDomainSocket = unixDomainSocket;
		this.socketType = SocketType.UNIX;
	}

	public PrintWriter getOutput() throws IOException {
		if (out == null) {

			switch (this.socketType) {
			case TCP:
				out = new PrintWriter(socket.getOutputStream(), true);
				break;
			case UNIX:
				out = new PrintWriter(unixDomainSocket.getOutputStream(), true);
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
				in = new BufferedReader(new InputStreamReader(unixDomainSocket.getInputStream()));
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
				unixDomainSocket.close();
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
