package net.lakis.apollo.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.Gson;

import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.history.FileHistory;
import net.lakis.apollo.config.SystemProperties;
import net.lakis.apollo.socket.SocketClient;
import net.lakis.apollo.socket.SocketConstants;
import net.lakis.apollo.socket.SocketGenerator;

public class Client {
	private SocketClient socketClient;
	private PrintWriter out;
	private BufferedReader in;
	private ConsoleReader reader;

	public Client() throws IOException {
		this.reader = new ConsoleReader();
		Runtime.getRuntime().addShutdownHook(new ClientShutdownHookup(this));

	}

	public void connect() {
		try {
			SocketGenerator socketGenerator = new SocketGenerator();
			this.socketClient = socketGenerator.generateClient();
			this.out = this.socketClient.getOutput();
			this.in = this.socketClient.getInput();

			println("Console successfully connected!");

		} catch (Exception e) {
			println("Console failed to connect!");

			if (socketClient != null) {
				socketClient.close();
			}
			socketClient = null;
		}
	}

	public void disconnect() {
		socketClient.close();
		socketClient = null;
		println("Console successfully disconnected!");
	}

	private void println(String msg) {
		try {
			if (reader == null)
				System.out.println(msg);
			else
				reader.println(msg);
		} catch (IOException e) {
			System.out.println(msg);
		}
	}

	// private void print(String msg) {
	// try {
	// if (reader == null)
	// System.out.print(msg);
	// else
	// reader.println(msg);
	// } catch (IOException e) {
	// System.out.print(msg);
	// }
	// }

	public void loop() throws IOException {

		reader.setHistory(new FileHistory(new File(SystemProperties.INSTANCE.getHistoryPath())));

		reloadOptions(reader);
		reader.setPrompt(SystemProperties.INSTANCE.getApolloName() + "> ");

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				break;
			} else if (line.equalsIgnoreCase("clear")) {
				reader.clearScreen();
			} else if (line.equalsIgnoreCase("start")) {
				try {
					String[] command = { SystemProperties.INSTANCE.getApolloScript(), "start" };
					// Process process = Runtime.getRuntime().exec(command);
					//
					// BufferedReader reader1 = new BufferedReader(new
					// InputStreamReader(process.getInputStream()));
					// String s;
					// while ((s = reader1.readLine()) != null) {
					// reader.println(s);
					// }
					Process process = new ProcessBuilder(command).inheritIO().start();
					process.waitFor();
				} catch (Exception e) {
					println(ExceptionUtils.getFullStackTrace(e));
				}
			} else if (line.equalsIgnoreCase("stop")) {
				try {
					if (this.socketClient != null) {
						disconnect();
						reloadOptions(reader);
					}
					String[] command = { SystemProperties.INSTANCE.getApolloScript(), "stop" };
					// Process process = Runtime.getRuntime().exec(command);
					// BufferedReader reader1 = new BufferedReader(new
					// InputStreamReader(process.getInputStream()));
					// String s;
					// while ((s = reader1.readLine()) != null) {
					// println(s);
					// }
					Process process = new ProcessBuilder(command).inheritIO().start();
					process.waitFor();
				} catch (Exception e) {
					println(ExceptionUtils.getFullStackTrace(e));
				}
			} else if (line.equalsIgnoreCase("status")) {
				try {
					String[] command = { SystemProperties.INSTANCE.getApolloScript(), "status" };
//				Process process = Runtime.getRuntime().exec(command);
//				BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				String s;
//				while ((s = reader1.readLine()) != null) {
//					println(s);
//				}
					Process process = new ProcessBuilder(command).inheritIO().start();
					process.waitFor();
				} catch (Exception e) {
					println(ExceptionUtils.getFullStackTrace(e));
				}
				if (this.socketClient == null// ||
												// this.socket.isInputShutdown()
												// ||
				// !this.socket.isConnected()
				// || !this.socket.isBound() || this.socket.isOutputShutdown()
				// || this.socket.isClosed()
				) {
					println("Console Disconnected!");

				} else
					println("Console Connected!");

			} else if (line.equalsIgnoreCase("restart")) {
				try {

					if (this.socketClient != null) {
						disconnect();
						reloadOptions(reader);
					}
					String[] command = { SystemProperties.INSTANCE.getApolloScript(), "stop" };

//				Process process = Runtime.getRuntime().exec(command);
//				BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				String s;
//				while ((s = reader1.readLine()) != null) {
//					reader.println(s);
//				}
					Process process = new ProcessBuilder(command).inheritIO().start();
					process.waitFor();
				} catch (Exception e) {
					println(ExceptionUtils.getFullStackTrace(e));
				}

				try {

//				command[1] = "start";
//				process = Runtime.getRuntime().exec(command);
//				reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				while ((s = reader1.readLine()) != null) {
//					reader.println(s);
//				}
					String[] command = { SystemProperties.INSTANCE.getApolloScript(), "start" };

					Process process = new ProcessBuilder(command).inheritIO().start();
					process.waitFor();
				} catch (Exception e) {
					println(ExceptionUtils.getFullStackTrace(e));
				}
			} else if (line.equalsIgnoreCase("connect")) {
				connect();
				reloadOptions(reader);
			} else if (line.equalsIgnoreCase("disconnect")) {
				disconnect();
				reloadOptions(reader);

			} // else if(line.startsWith("log ")) {
				// if(this.logHandler.handle(line))
				// {
				// if (this.socket == null) {
				// println("Console is disconnected. please connect it to reload
				// log!");
				// }
				// else
				// {
				// this.out.println("reloadlog");
				// }
				// }
				// }
			else {

				if (this.socketClient == null) {
					println("Console is disconnected. please connect it before sending any request!");
				} else {
					this.out.println(line);
					redirectToConsole();
				}
			}

		}
	}

	private void redirectToConsole() {
		try {
			while (true) {
				String result = this.in.readLine();
				if (result == null || (this.socketClient == null) // ||
				// this.socket.isInputShutdown()
				// ||
				// !this.socket.isConnected()
				// || !this.socket.isBound() || this.socket.isOutputShutdown()
				// || this.socket.isClosed()
				) {
					println("Connection lost!");
					this.socketClient = null;
					this.in = null;
					this.out = null;
					return;
				}

				if (result.equals("<<<end>>>"))
					return;
				println(result);

			}
		} catch (IOException e) {
			println("Connection lost!");
			println(ExceptionUtils.getFullStackTrace(e));

			this.socketClient = null;
			this.in = null;
			this.out = null;
			return;
		}
	}

	private void reloadOptions(ConsoleReader reader) throws IOException {

		Collection<Completer> completers = reader.getCompleters();
		for (Completer c : completers)
			reader.removeCompleter(c);

		List<Completer> completers3d = new ArrayList<Completer>();

		List<Completer> completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("start"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("stop"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("status"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("restart"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("clear"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("connect"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("disconnect"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("quit"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));

		completers2d = new ArrayList<Completer>();
		completers2d.add(new StringsCompleter("exit"));
		completers2d.add(new NullCompleter());
		completers3d.add(new ArgumentCompleter(completers2d));
		if (this.socketClient != null) {
			this.out.println("getoptions");
			String result = this.in.readLine();
			String[][][] data = new Gson().fromJson(result, String[][][].class);

			for (String[][] Arr3d : data) {
				completers2d = new ArrayList<Completer>();
				for (String[] Arr2d : Arr3d) {
					completers2d.add(new StringsCompleter(Arr2d));
				}
				completers2d.add(new NullCompleter());
				completers3d.add(new ArgumentCompleter(completers2d));
			}
		}

		reader.addCompleter(new AggregateCompleter(completers3d));

	}

	public ConsoleReader getReader() {
		return reader;
	}

	public void setReader(ConsoleReader reader) {
		this.reader = reader;
	}

	public static void standaloneRequest(String cmd) {
		SocketClient socketClient = null;
		try {
			SocketGenerator socketGenerator = new SocketGenerator();

			socketClient = socketGenerator.generateClient();
			PrintWriter out = socketClient.getOutput();
			BufferedReader in = socketClient.getInput();
			out.println(cmd);
			while (true) {
				String result = in.readLine();
				if (result == null || socketClient == null) {
					System.err.println("Connection lost!");
					return;
				}

				if (result.equals(SocketConstants.END_OF_STREAM))
					return;
				System.out.println(result);

			}
		} catch (Exception e) {
			System.err.println(ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (socketClient != null)
				socketClient.close();

		}

	}
}
