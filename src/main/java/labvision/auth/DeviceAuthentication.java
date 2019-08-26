package labvision.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import labvision.LabVisionConfig;
import labvision.LabVisionDataAccess;
import labvision.entities.Device;
import labvision.entities.User;

/**
 * Device-based authentication for users who select "Remember Me"
 * @author davidnisson
 *
 */
public class DeviceAuthentication {
	private static final String DEVICE_TOKEN_COOKIE_NAME = "dt";
	private LabVisionConfig config;
	private LabVisionDataAccess dataAccess;
	
	public DeviceAuthentication(LabVisionConfig config, LabVisionDataAccess dataAccess) {
		this.config = config;
		this.dataAccess = dataAccess;
	}

	/**
	 * Retrieves a device token from an HTTP request
	 * @param req the request object
	 * @return the token
	 */
	public static DeviceToken getDeviceToken(HttpServletRequest req) {
		return DeviceToken.parseDeviceToken(Stream.of(req.getCookies())
				.filter(c -> c.getName().equals(DEVICE_TOKEN_COOKIE_NAME))
				.map(c -> c.getValue())
				.findAny()
				.orElse(null));
	}

	/**
	 * Clear a device token from a client
	 * @param resp the HTTP response object
	 */
	public static void clearDeviceToken(HttpServletResponse resp) {
		Cookie cookie = new Cookie(DEVICE_TOKEN_COOKIE_NAME, "");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
	}

	public DeviceToken createDeviceToken(User user, HttpServletRequest req) throws IOException {
		// first add the device to the database
		Device device = dataAccess.addNewDevice(user);
		
		// then create the device token
		OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC)
				.plusSeconds(config.getDeviceTokenExpirationTime());
		
		// finally get it signed
		DeviceToken unsignedToken = new DeviceToken(user.getId(), device.getId(), expiration, null);
		String deviceTokenSignerUrl = config.getDeviceTokenSignerUrl();
		if (deviceTokenSignerUrl == null) {
			throw new RuntimeException("Device token signer not set");
		}
		
		URL signerURL = new URL(deviceTokenSignerUrl);
		HttpsURLConnection conn = (HttpsURLConnection)signerURL.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Length", String.valueOf(unsignedToken.getDataBytes().length));
		conn.setRequestProperty("Content-Type", "text/plain");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		
		Base64.Encoder encoder = Base64.getEncoder();
		String dataString = encoder.encodeToString(unsignedToken.getDataBytes());
		
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeUTF(dataString);
		out.close();
		
		Base64.Decoder decoder = Base64.getDecoder();
		
		DataInputStream in = new DataInputStream(conn.getInputStream());
		String encSignature = in.readUTF();
		in.close();
		
		byte[] signature = decoder.decode(encSignature);
		
		return new DeviceToken(user.getId(), device.getId(), expiration, signature);
	}

	public void addDeviceToken(HttpServletResponse resp, DeviceToken deviceToken) {
		Cookie cookie = new Cookie(DEVICE_TOKEN_COOKIE_NAME, deviceToken.toString());
		if (config.getDeviceTokenExpirationTime() <= Integer.MAX_VALUE) {
			cookie.setMaxAge((int)config.getDeviceTokenExpirationTime());
		}
		resp.addCookie(cookie);
	}

	public boolean verifyDeviceToken(DeviceToken deviceToken, User user, HttpServletRequest req) 
	throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		// load the public key
		FileInputStream keyIn = new FileInputStream(config.getPublicKeyFilename());
		byte[] encKey = new byte[keyIn.available()];
		keyIn.read(encKey);
		
		keyIn.close();
		
		// convert the public key
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encKey);
		KeyFactory keyFactory = KeyFactory.getInstance(config.getDeviceTokenKeyAlgorithm());
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		
		// verify the signature
		Signature signature = Signature.getInstance(config.getDeviceTokenKeyAlgorithm());
		signature.initVerify(pubKey);
		signature.update(deviceToken.getDataBytes());
		if (!signature.verify(deviceToken.getSignature())) {
			return false;
		}
		
		// verify the information in the token
		if (deviceToken.getUserId() != user.getId()) {
			return false;
		}
		if (user.getDevices().stream()
				.map(Device::getId)
				.noneMatch(id -> id.equals(deviceToken.getDeviceId()))) {
			return false;
		}
		if (OffsetDateTime.now(ZoneOffset.UTC).isAfter(deviceToken.getExpiration())) {
			return false;
		}
		
		return true;
	}
	
}
