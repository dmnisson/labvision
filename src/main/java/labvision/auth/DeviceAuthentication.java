package labvision.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import labvision.LabVisionConfig;
import labvision.LabVisionServletContextListener;
import labvision.entities.Device;
import labvision.entities.User;

/**
 * Device-based authentication for users who select "Remember Me"
 * @author davidnisson
 *
 */
public class DeviceAuthentication {
	private static final String DEVICE_TOKEN_COOKIE_NAME = "dt";
	private final LabVisionConfig config;
	
	public DeviceAuthentication(LabVisionConfig config) {
		this.config = config;
	}

	/**
	 * Retrieves a device token from an HTTP request
	 * @param req the request object
	 * @return the token
	 */
	public static DeviceToken getDeviceToken(HttpServletRequest req) {
		String deviceTokenString = Stream.of(req.getCookies())
				.filter(c -> c.getName().equals(DEVICE_TOKEN_COOKIE_NAME))
				.map(c -> c.getValue())
				.findAny()
				.orElse(null);
		if (deviceTokenString == null) {
			return null;
		} else {
			return DeviceToken.parseDeviceToken(deviceTokenString);
		}
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
	
	public DeviceToken createDeviceToken(Device device, User user, HttpServletRequest req) throws IOException {
		OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC)
				.plusSeconds(config.getDeviceTokenExpirationTime());
		
		// get the token signed
		DeviceToken unsignedToken = new DeviceToken(user.getId(), device.getId(), expiration, null);
		String deviceTokenSignerUrl = config.getDeviceTokenSignerUrl();
		byte[] signature;
		if (deviceTokenSignerUrl == null) {
			// sign the token using the built-in modules
			DeviceTokenSigning signing = (DeviceTokenSigning) req.getServletContext()
					.getAttribute(LabVisionServletContextListener.DEVICE_TOKEN_SIGNING_ATTR);
			try {
				signature = signing.getSignature(unsignedToken.getDataBytes());
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnrecoverableKeyException | KeyStoreException | CertificateException e) {
				throw new RuntimeException(e);
			}
		} else {
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
			
			signature = decoder.decode(encSignature);
		}
		
		return new DeviceToken(user.getId(), device.getId(), expiration, signature);
	}

	public void addDeviceToken(HttpServletResponse resp, DeviceToken deviceToken) {
		Cookie cookie = new Cookie(DEVICE_TOKEN_COOKIE_NAME, deviceToken.toString());
		if (config.getDeviceTokenExpirationTime() <= Integer.MAX_VALUE) {
			cookie.setMaxAge((int)config.getDeviceTokenExpirationTime());
		}
		resp.addCookie(cookie);
	}

	public boolean verifyDeviceToken(DeviceToken deviceToken, User user, HttpServletRequest req) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, InvalidKeyException, SignatureException {
		// load the keystore
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(
			new FileInputStream(config.getDeviceTokenKeystoreFilename()),
			config.getDeviceTokenKeystorePassword().toCharArray()
		);
		
		Certificate certificate = keyStore.getCertificate("devauth");
		PublicKey pubKey = certificate.getPublicKey();
		
		// verify the signature
		Signature signature = Signature.getInstance(config.getDeviceTokenSignatureAlgorithm());
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
