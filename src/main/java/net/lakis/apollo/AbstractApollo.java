package net.lakis.apollo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractApollo {

	private static final Logger log = LogManager.getLogger(AbstractApollo.class);
 	private List<IConsoleHandler> consoleHandlers;
	private long pid;

	@SuppressWarnings("unchecked")
	public static <T> T getHandler(Class<T> clazz) {

		for (Object handler : App.getApollo().getConsoleHandlers()) {
			if (clazz.isAssignableFrom(handler.getClass())) {
				return (T) handler;
			}
		}
		return null;

	}

	public AbstractApollo() {
 		this.consoleHandlers = new ArrayList<IConsoleHandler>();
		this.consoleHandlers.add(new SystemHandler());
	}

	public void safeInit() {
		try {
			this.init();
		} catch (Exception e) {
			log.error("Exception: ", e);
			System.exit(3);
		}
	}

	public void safeOnStart() {
		try {
			this.onStart();
		} catch (Exception e) {
			log.error("Exception: ", e);
			System.exit(3);
		}
	}

	public void safeOnStop() {
		try {
			this.onStop();
		} catch (Exception e) {
			log.error("Exception: ", e);
			System.exit(3);
		}
	}

	public abstract void init() throws Exception;

	public abstract void onStart() throws Exception;

	public abstract void onStop() throws Exception;

	public List<IConsoleHandler> getConsoleHandlers() {
		return consoleHandlers;
	}

	public void addConsoleHandler(IConsoleHandler handler) {
		this.consoleHandlers.add(handler);
	}

	public void removeConsoleHandler(IConsoleHandler handler) {
		this.consoleHandlers.remove(handler);
	}

 

	public void onInit() {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (IConsoleHandler.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					this.consoleHandlers.add((IConsoleHandler) field.get(this));
				} catch (Exception e) {
				}
			}
		}
		for (IConsoleHandler handler : consoleHandlers) {
			handler.onInit();

		}

	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

}
