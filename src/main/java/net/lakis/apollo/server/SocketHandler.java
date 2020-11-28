package net.lakis.apollo.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.Gson;

import net.lakis.apollo.AbstractApollo;
import net.lakis.apollo.IConsoleHandler;
import net.lakis.apollo.annotations.ConsoleKey;
import net.lakis.apollo.annotations.ConsoleUsage;
import net.lakis.apollo.socket.Socket;
import net.lakis.apollo.socket.SocketConstants;

 
public class SocketHandler implements Runnable {
	private AbstractApollo service;
	private Socket socket;

	public SocketHandler(Socket socket, AbstractApollo service) {
		super();
		this.socket = socket;
		this.service = service;

	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			out = socket.getOutput();
			in = socket.getInput();

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				// if (inputLine.equalsIgnoreCase("bye") ||
				// inputLine.equalsIgnoreCase("quit")
				// || inputLine.equalsIgnoreCase("exit"))
				// break;
				inputLine = inputLine.trim();
				List<IConsoleHandler> handlers = service.getConsoleHandlers();

				if (inputLine.equalsIgnoreCase("getoptions")) {

					List<List<List<String>>> ret = new ArrayList<List<List<String>>>();

					for (IConsoleHandler handler : handlers) {
						List<List<String>> classOptions = new ArrayList<List<String>>();
						ConsoleKey consoleKey = handler.getClass().getAnnotation(ConsoleKey.class);
						if (consoleKey == null)
							continue;
						List<String> methodOptions = new ArrayList<String>();
						methodOptions.add(consoleKey.value());
						classOptions.add(methodOptions);

						methodOptions = new ArrayList<String>();
						for (Method method : handler.getClass().getMethods()) {

							consoleKey = method.getAnnotation(ConsoleKey.class);

							if (consoleKey == null)
								continue;
							methodOptions.add(consoleKey.value());

						}

						if (methodOptions.size() > 0) {
							classOptions.add(methodOptions);
							ret.add(classOptions);
						}
					}
					out.println(new Gson().toJson(ret));
				} else {
					handle(out, inputLine);
				}
			}

		} catch (Exception e) {

			out.println(ExceptionUtils.getFullStackTrace(e));
		} finally {
			socket.close();
		}
	}

	
	public List<String> splitLine(String line) {
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
		while (m.find())
		    list.add(m.group(1).replace("\"", ""));
		return list;

	}
	private void handle(PrintWriter out, String inputLine) {
		try {
			StringBuilder sb = new StringBuilder();
//			String[] args = inputLine.trim().split("\\s+");
			List<String> argsList = splitLine(inputLine);
			String[] args = new String[argsList.size()];
			args = argsList.toArray(args);

			for (Object o : service.getConsoleHandlers()) {
				ConsoleKey key = null;
				try {
					 key = o.getClass().getAnnotation(ConsoleKey.class);
				}catch (Exception e) {
					continue;
 				}
				 
				if (key == null || !key.value().equalsIgnoreCase(args[0]))
					continue;
				if (args.length < 2) {
					sb.append(args[0]);

					sb.append(" requires a method: \n");
					for (Method m : o.getClass().getMethods()) {
						key = m.getAnnotation(ConsoleKey.class);
						if (key == null)
							continue;
						sb.append(key.value());
						sb.append(" ");
					}
					sb.append("\n");

					out.println(sb.toString());
					out.println(SocketConstants.END_OF_STREAM);
					return;
				}

				for (Method m : o.getClass().getMethods()) {
					key = m.getAnnotation(ConsoleKey.class);
					if (key == null || !key.value().equalsIgnoreCase(args[1]))
						continue;

					Class<?>[] params = m.getParameterTypes();

					if (params.length != args.length - 2
							&& (params.length != args.length - 1 || params[0] != PrintWriter.class)) {
						sb.append(args[0]);
						sb.append(" ");
						sb.append(args[1]);

						if (m.getParameterTypes().length != 1) {
							sb.append(" requires ");
							sb.append(m.getParameterTypes().length);
							sb.append(" parameters\n");
						} else
							sb.append(" requires 1 parameter\n");

						ConsoleUsage usage = m.getAnnotation(ConsoleUsage.class);
						if (usage != null) {

							sb.append("\nUsage: ");
							sb.append(args[0]);
							sb.append(" ");
							sb.append(args[1]);
							sb.append(" ");
							sb.append(usage.value());
							sb.append("\n");
						}
						out.println(sb.toString());
						out.println(SocketConstants.END_OF_STREAM);
						return;
					}
					Object[] parameters = new Object[params.length];
					int i = 0;
					if (params.length == args.length - 1)
						parameters[i++] = out;
					for (int j = 2; j < args.length; j++)
						parameters[i++] = args[j];
					if (params.length == args.length - 2) {
						String ret = (String) m.invoke(o, parameters);
						out.println(ret);
						out.println(SocketConstants.END_OF_STREAM);
					} else {
						m.invoke(o, parameters);
					}
					return;
				}
			}
			sb.append("Command not found: ");
			sb.append(inputLine);
			sb.append("\n");
			out.println(sb.toString());
		} catch (Exception e) {
			out.println(ExceptionUtils.getFullStackTrace(e));
		}
		out.println(SocketConstants.END_OF_STREAM);

	}

}
