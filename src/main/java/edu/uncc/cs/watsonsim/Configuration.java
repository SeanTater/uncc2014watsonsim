package edu.uncc.cs.watsonsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {

	protected final String data_path = "data/";
	public final Map<String, String> config;

	@SuppressWarnings({ "unchecked", "rawtypes" }) // From Properties -> Map
	public Configuration() {
		/*
		 * Normally, wrapping a IOException with a RuntimeException is bad
		 * but if you cannot find a configuration file many bad things will
		 * happen, and basically every useful feature will fail. So you might
		 * as well just quit here.
		 */
		try {
			// Check the data path
			File f = new File(data_path);
			if (!(f.exists() && f.isDirectory())) {
				throw new IOException(data_path + " should be a directory.");
			}
			
			// Read the configuration
			Properties props = null;
			for (String prefix : new String[]{this.data_path, ""}) {
				try (Reader s = new InputStreamReader(
						new FileInputStream(prefix + "config.properties"), "UTF-8")){
					// Make it, then link it if it works.
					Properties _local_props = new Properties();
					_local_props.load(s);
					props = _local_props;
				} catch (FileNotFoundException e) {
					// This is only an error if none are found.
				}
			}
			// If it didn't link, all the reads failed.
			if (props == null) {
				throw new IOException("Failed to read config.properties in either "
						+ this.data_path
						+ " or "
						+ System.getProperty("user.dir") // CWD
						+ " You can create one by making a copy of"
						+ " config.properties.sample. Check the README as well.");
			}
			// Now make properties immutable.
			Map<Object, Object> m = new HashMap<>();
			m.putAll(props);
			this.config = Collections.unmodifiableMap((Map) m);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convenience method for getting a setting.
	 * @param config Map from the configuration file (config.properties) 
	 * @param key The key that must exist in the properties
	 * @return The non-null String value, or else throw a RuntimeException.
	 */
	public String getConfOrDie(String key) {
		String value = config.get(key);
		if (value == null) throw new RuntimeException("Required key (" + key + ") missing from configuration file.");
		return value;
	}

	/**
	 * Get the path to a resource, ensuring it exists.
	 * This is mostly to give helpful errors and fail fast if you missed a
	 * step setting up.
	 * @param resource The relative path of the resource without leading /
	 */
	public String pathMustExist(String resource) {
		String path = data_path + File.separator + resource;
		if (!new File(path).exists()) {
			throw new RuntimeException("The data directory is missing the"
					+ " expected resource: " + path);
		}
		return path;
	}

}