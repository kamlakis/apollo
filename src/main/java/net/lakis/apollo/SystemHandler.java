package net.lakis.apollo;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import net.lakis.apollo.annotations.ConsoleKey;

@ConsoleKey("system")
public class SystemHandler implements IConsoleHandler {

	private Properties prop;

	@ConsoleKey("getHeapSize")
	public String getHeapSize() {
		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this
		// size.// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will
		// increase // after garbage collection and decrease as new objects are
		// created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		return "heapSize: " + heapSize + " Bytes\nheapMaxSize: " + heapMaxSize + " Bytes\nheapFreeSize: " + heapFreeSize
				+ " Bytes";
	}

	@ConsoleKey("getThreadsCount")
	public String getThreadsCount() {
		return "threadsCount: " + Thread.activeCount();
	}

	@ConsoleKey("collectGarbage")
	public String collectGarbage() {
		System.gc();
		return "Garbage collector called";
	}

	@ConsoleKey("getBuildInfo")
	public String getBuildInfo() {
		StringBuilder sb = new StringBuilder();

		for (Object key : prop.keySet()) {
			if (key.toString().startsWith("build")) {
				sb.append(StringUtils.rightPad(key.toString(), 29));
				sb.append(": ");
				sb.append(prop.get(key));
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();

	}

	@ConsoleKey("getGitInfo")
	public String getGitInfo() {
		StringBuilder sb = new StringBuilder();

		for (Object key : prop.keySet()) {
			if (key.toString().startsWith("git")) {
				sb.append(StringUtils.rightPad(key.toString(), 29));
				sb.append(": ");
				sb.append(prop.get(key));
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();

	}

	@ConsoleKey("getPID")
	public String getPID() {
		return String.valueOf(App.getApollo().getPid());
	}

	public void onInit() {
		this.prop = new Properties();

		try (InputStream in = SystemHandler.class.getResourceAsStream("/apollo.conf");) {
			prop.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
