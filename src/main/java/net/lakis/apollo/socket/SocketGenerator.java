package net.lakis.apollo.socket;

import java.io.IOException;
import java.net.UnknownHostException;

import net.lakis.apollo.config.SystemProperties;

public class SocketGenerator {
 
	private String path;
	private String host;
	private int port;

	public SocketGenerator()
	{
		this.path = SystemProperties.INSTANCE.getSocketFile();
		this.host = SystemProperties.INSTANCE.getSocketHost();
		this.port  = SystemProperties.INSTANCE.getSocketPort();
	}
	
	
	public SocketServer generateServer() throws Exception {
 
		if (path != null) 
			return new SocketServer(path);
		else
 			return new SocketServer(host, port);
	 
	}

	public SocketClient generateClient() throws Exception {

 
		if (path != null) 
			return generateClient(path);
		else
			return generateClient(host, port);
		 

	}

	private SocketClient generateClient(String path) throws IOException {
		return new SocketClient(path);
	}

	private SocketClient generateClient(String host, int port) throws UnknownHostException, IOException {
		return new SocketClient(host, port);
	}
}
