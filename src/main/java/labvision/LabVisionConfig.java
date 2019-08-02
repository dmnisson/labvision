package labvision;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access to configuration file
 * @author davidnisson
 *
 */
public class LabVisionConfig {
	private Properties props = new Properties();
	
	public static final String APP_VERSION = "0.0.0";
	
	public static final String APP_NAME = "LabVision";
	
	// property names
	private static final String PERSISTENCE_UNIT_KEY = "persistenceUnit";

	private static final String VERSION_KEY = "version";
	
	private static final String PASSWORD_HASH_ALGORITHM_KEY = "passwordHashAlgorithm";
	
	public LabVisionConfig(String configPath) {
		try {
			props.load(new FileInputStream(configPath));
		} catch (FileNotFoundException e) {
			Logger.getLogger(LabVisionConfig.APP_NAME).log(
					Level.WARNING, 
					"Could not find app.properties file, using defaults",
					e);
		} catch (IOException e) {
			Logger.getLogger(LabVisionConfig.APP_NAME).log(
					Level.WARNING, 
					"Could not read app.properties file, using defaults",
					e);
		}
		
		// check version number
		String version = props.getProperty(VERSION_KEY);
		if (version != APP_VERSION) {
			Logger.getLogger(LabVisionConfig.APP_NAME).log(
					Level.WARNING,
					"Version in app.properties file, " + 
					version + ", does not match current version, " + 
							APP_VERSION);
		}
	}

	public String getPersistenceUnitName() {
		return props.getProperty(PERSISTENCE_UNIT_KEY);
	}
	
	public String getPasswordHashAlgorithm() {
		return props.getProperty(PASSWORD_HASH_ALGORITHM_KEY, "SHA-256");
	}
}
