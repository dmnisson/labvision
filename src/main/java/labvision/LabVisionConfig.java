package labvision;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Access to configuration file
 * @author davidnisson
 *
 */
public class LabVisionConfig {
	private static final String STUDENT_DASHBOARD_MAX_RECENT_COURSES_KEY = "studentDashboardMaxRecentCourses";

	private static final String STUDENT_DASHBOARD_MAX_RECENT_EXPERIMENTS_KEY = "studentDashboardMaxRecentExperiments";

	private Properties props = new Properties();
	
	public static final String APP_VERSION = "0.0.0";
	
	public static final String APP_NAME = "LabVision";
	
	public static final String DEFAULT_PERSISTENCE_UNIT = "LabVisionDefaultPersistence";
	
	public static final String TESTING_PERSISTENCE_UNIT_NAME = "LabVisionTestingPersistence";
	
	// property names
	private static final String PERSISTENCE_UNIT_KEY = "persistenceUnit";

	private static final String VERSION_KEY = "version";
	
	private static final String PASSWORD_HASH_ALGORITHM_KEY = "passwordHashAlgorithm";

	private static final String PASSWORD_SALT_SIZE_KEY = "passwordSaltSize";
	
	private static final String DEVICE_TOKEN_EXPIRATION_TIME_KEY = "deviceTokenExpirationTime";
	
	private static final String DEVICE_TOKEN_SIGNER_URL_KEY = "deviceTokenSignerUrl";
	
	private static final String PUBLIC_KEY_FILENAME_KEY = "publicKeyFilename";
	
	private static final String DEVICE_TOKEN_KEY_ALGORITHM_NAME_KEY = "deviceTokenKeyAlgorithm";

	private static final String REPORT_UPLOAD_FILE_PATH_KEY = "reportUploadFilePath";
	
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
		String version = props.getProperty(VERSION_KEY, APP_VERSION);
		if (version != APP_VERSION) {
			Logger.getLogger(LabVisionConfig.APP_NAME).log(
					Level.WARNING,
					"Version in app.properties file, " + 
					version + ", does not match current version, " + 
							APP_VERSION);
		}
	}

	public String getPersistenceUnitName() {
		return props.getProperty(PERSISTENCE_UNIT_KEY, DEFAULT_PERSISTENCE_UNIT);
	}
	
	public String getPasswordHashAlgorithm() {
		return props.getProperty(PASSWORD_HASH_ALGORITHM_KEY, "SHA-256");
	}

	public int getSaltSize() {
		return Integer.parseInt(props.getProperty(PASSWORD_SALT_SIZE_KEY, "16"));
	}

	public long getDeviceTokenExpirationTime() {
		return Long.parseLong(props.getProperty(
				DEVICE_TOKEN_EXPIRATION_TIME_KEY, 
				Long.toString(Duration.ofDays(7).getSeconds()))
				);
	}

	public String getDeviceTokenSignerUrl() {
		return props.getProperty(DEVICE_TOKEN_SIGNER_URL_KEY);
	}

	public String getPublicKeyFilename() {
		return props.getProperty(PUBLIC_KEY_FILENAME_KEY, "devauth.cer");
	}

	public String getDeviceTokenKeyAlgorithm() {
		return props.getProperty(DEVICE_TOKEN_KEY_ALGORITHM_NAME_KEY, "SHA256withRSA");
	}

	public int getStudentDashboardMaxRecentExperiments() {
		return Integer.parseInt(props.getProperty(STUDENT_DASHBOARD_MAX_RECENT_EXPERIMENTS_KEY, "5"));
	}

	public int getStudentDashboardMaxRecentCourses() {
		return Integer.parseInt(props.getProperty(STUDENT_DASHBOARD_MAX_RECENT_COURSES_KEY, "5"));
	}

	public String getReportUploadFilePath() {
		return props.getProperty(REPORT_UPLOAD_FILE_PATH_KEY,
				Paths.get("reports").toAbsolutePath().toString());
	}
}
