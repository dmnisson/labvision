package labvision.auth;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * A token used to identify a user's device
 * @author davidnisson
 *
 */
public class DeviceToken {
	private final int userId;
	private final String deviceId;
	private final OffsetDateTime expiration;
	private final byte[] signature;
	
	public DeviceToken(int userId, String deviceId, OffsetDateTime expiration, byte[] signature) {
		this.userId = userId;
		this.deviceId = deviceId;
		this.expiration = expiration;
		if (Objects.isNull(signature)) {
			this.signature = null;
		} else {
			this.signature = Arrays.copyOf(signature, signature.length);
		}
	}

	public int getUserId() {
		return userId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public OffsetDateTime getExpiration() {
		return expiration;
	}

	public static DeviceToken parseDeviceToken(String tokenString) {
		String[] tokenParts = tokenString.split(";");
		
		Base64.Decoder decoder = Base64.getDecoder();
		
		byte[] userIdBytes = decoder.decode(tokenParts[0]);
		int userId = ByteBuffer.wrap(userIdBytes).getInt();
		
		String deviceId = tokenParts[1];
		
		OffsetDateTime expiration = OffsetDateTime.parse(tokenParts[2]);
		
		byte[] signature = null;
		if (tokenParts.length > 3) {
			signature = decoder.decode(tokenParts[3]);
		};
		
		return new DeviceToken(userId, deviceId, expiration, signature);
	}
	
	@Override
	public String toString() {
		Base64.Encoder encoder = Base64.getEncoder();
		
		String signatureString;
		
		if (signature == null) {
			signatureString = "";
		} else {
			signatureString = encoder.encodeToString(signature);
		}
		
		return encoder.encodeToString(ByteBuffer.allocate(Integer.BYTES)
				.putInt(userId)
				.array()) + ";" +
				deviceId + ";" +
				expiration.toString() + ";" +
				signatureString;
	}

	public byte[] getSignature() {
		return signature;
	}

	public byte[] getDataBytes() {
		return this.toString().getBytes(Charset.forName("UTF-8"));
	}
}
