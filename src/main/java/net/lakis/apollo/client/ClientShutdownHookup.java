package net.lakis.apollo.client;

import java.io.File;
import java.io.FileWriter;

import jline.console.ConsoleReader;
import jline.console.history.History;
import jline.console.history.History.Entry;
import net.lakis.apollo.config.SystemProperties;

public class ClientShutdownHookup extends Thread {

	private Client client;

	public ClientShutdownHookup(Client client) {
		this.client = client;
	}

	public void run() {
		
		try {
			ConsoleReader reader = client.getReader();
			if (reader == null)
				return;
			History history = reader.getHistory();
			if (history == null)
				return;

			File file = new File(SystemProperties.INSTANCE.getHistoryPath());
			if(file.exists())
				file.delete();
			FileWriter writer = new FileWriter(file, true);
			
			for (Entry element : history) {
				writer.append(element.value());
				writer.append("\n");
			}
			writer.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}
