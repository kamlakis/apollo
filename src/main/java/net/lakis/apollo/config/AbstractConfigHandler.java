package net.lakis.apollo.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import net.lakis.apollo.IConsoleHandler;
import net.lakis.apollo.annotations.ConsoleKey;

@ConsoleKey("config")
public abstract class AbstractConfigHandler<T> implements IConsoleHandler {

	private static final Logger log = LogManager.getLogger(AbstractConfigHandler.class);

	private final Class<T> typeParameterClass;
	private T config;

	public AbstractConfigHandler(Class<T> typeParameterClass) throws InstantiationException, IllegalAccessException {
		this.typeParameterClass = typeParameterClass;
		reloadConfig();

	}

	@ConsoleKey("reload")
	public String reloadConfig() throws InstantiationException, IllegalAccessException {
		try {

			this.config = typeParameterClass.newInstance();
			for (Field field : typeParameterClass.getDeclaredFields()) {
				Object conf = null;
				try {

					String confPath = SystemProperties.INSTANCE.getUserPath();
					confPath = System.getProperty("user.dir", "D:\\test");

					String path = confPath + "/conf/" + field.getName() + ".json";
					InputStream inputStream = new FileInputStream(path);
					try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
						conf = new Gson().fromJson(reader, field.getType());
					}
				} catch (Exception e) {
					log.warn(field.getName() + ".json doesn't exist or corrupted");
					conf = field.getType().newInstance();
				}
				field.setAccessible(true);
				field.set(config, conf);
			}

		} catch (Exception e) {
			log.error("Exception: ", e);
			this.config = typeParameterClass.newInstance();
		}
		return "Configurations successfully reloaded.";
	}

	@ConsoleKey("print")
	public String print() {
		return config.toString();
	}

	@ConsoleKey("printJson")
	public String printJson() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(config);
	}

	public T getConfig() {
		return config;
	}

	public void onInit() {
	}

}
