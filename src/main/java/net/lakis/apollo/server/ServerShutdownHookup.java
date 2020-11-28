package net.lakis.apollo.server;

import net.lakis.apollo.AbstractApollo;

public class ServerShutdownHookup extends Thread {

	private AbstractApollo service;

	public ServerShutdownHookup(AbstractApollo service) {
		this.service = service;
	}

	@Override
	public void run() {
		service.safeOnStop();
	}

}
