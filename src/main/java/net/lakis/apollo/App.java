package net.lakis.apollo;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;

import net.lakis.apollo.client.Client;
import net.lakis.apollo.config.SystemProperties;
import net.lakis.apollo.server.Server;

public class App {

	private static AbstractApollo apollo;

 
	public static void main(String[] args) throws Exception {
		App app = new App( );

		if (args.length == 0) {
			app.connect();
		} else if (args[0].equalsIgnoreCase("run")) {
			app.run();
		} else {
			app.request(args);
		}
	}

	private void request(String[] args) throws IOException {
		String str = String.join(" ", args);
		Client.standaloneRequest(str);
	}

	private void connect() throws UnknownHostException, IOException {
		final Client client = new Client();
		client.connect();
		client.loop();

	}

	private void writePidToFile() {

		// Note: may fail in some JVM implementations

		// something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println(jvmName);
		final int index = jvmName.indexOf('@');

		if (index < 1) {
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			return;
		}

		try {
			long pid = Long.parseLong(jvmName.substring(0, index));
			apollo.setPid(pid);
			String path = SystemProperties.INSTANCE.getPidPath();
			if (StringUtils.isNotBlank(path)) {
				try (FileWriter fileWriter = new FileWriter(path, false)) {
					fileWriter.write(String.valueOf(pid));
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	private void run() throws Exception {

		Class<?> clazz = Class.forName(SystemProperties.INSTANCE.getApolloClass());
		Constructor<?> ctor = clazz.getConstructor();
		App.apollo = (AbstractApollo) ctor.newInstance();

		writePidToFile();
		apollo.safeInit();
		apollo.onInit();
		apollo.safeOnStart();
		Server appInterfaceServer = new Server(apollo);
		appInterfaceServer.start();
	}

	public static AbstractApollo getApollo() {
		return apollo;
	}

}
