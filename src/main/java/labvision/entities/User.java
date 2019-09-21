package labvision.entities;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import labvision.LabVisionConfig;
import labvision.utils.ByteArrayStringConverter;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class User implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	
	/**
	 * The username
	 */
	protected String username;
	
	/**
	 * The (password + salt) digest in hexadecimal format
	 */
	protected String passwordDigest;
	
	/**
	 * The salt in hexadecimal format
	 */
	protected String passwordSalt;
	
	/**
	 * The hash algorithm used to compute this user's password
	 */
	protected String hashAlgorithm;
	
	/**
	 * The user's devices
	 */
	@OneToMany( mappedBy="user", targetEntity = Device.class )
	private List<Device> devices;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	/**
	 * Get the password digest
	 * @return hexadecimal string representation
	 */
	public String getPasswordDigest() {
		return passwordDigest;
	}

	/**
	 * Set a new password digest
	 * @param passwordDigest hexadecimal string representation
	 */
	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}
	
	/**
	 * Get the password digest
	 * @return array of bytes in digest
	 */
	public byte[] getPasswordDigestBytes() {
		return ByteArrayStringConverter.hexToByteArray(this.passwordDigest);
	}
	
	/**
	 * Set a new password digest
	 * @param passwordDigest array of bytes in digest
	 */
	public void setPasswordDigestBytes(byte[] passwordDigest) {
		this.passwordDigest = ByteArrayStringConverter.toHexString(passwordDigest);
	}

	/**
	 * Get the password salt
	 * @return hexadecimal string representation
	 */
	public String getPasswordSalt() {
		return passwordSalt;
	}

	/**
	 * Set a new password salt
	 * @param passwordSalt hexadecimal string representation
	 */
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
	
	/**
	 * Get the password salt
	 * @return array of bytes in salt
	 */
	public byte[] getPasswordSaltBytes() {
		return ByteArrayStringConverter.hexToByteArray(this.passwordSalt);
	}
	
	/**
	 * Set a new password salt
	 * @param passwordSalt new salt as array of bytes
	 */
	public void setPasswordSaltBytes(byte[] passwordSalt) {
		this.passwordSalt = ByteArrayStringConverter.toHexString(passwordSalt);
	}
	
	// Hash a password with the given salt.
	// TODO Use PBDKDF2WithHmacSHA1
	private static byte[] hashPassword(
			String password, 
			byte[] salt, 
			String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
		
		messageDigest.update(password.getBytes(Charset.forName("UTF-8")));
		
		byte[] digest = messageDigest.digest(salt);
		return digest;
	}
	
	/**
	 * Check if password matches the one stored as a hash
	 * @param password the password
	 * @return true if matches
	 * @throws NoSuchAlgorithmException if the stored hash algorithm is not supported
	 */
	public boolean passwordMatches(String password) 
			throws NoSuchAlgorithmException {
		return MessageDigest.isEqual(
				this.getPasswordDigestBytes(), 
				hashPassword(password, this.getPasswordSaltBytes(), this.getHashAlgorithm())
				);
	}
	
	/**
	 * Update the password hash for a new password
	 * @param config the configuration to use
	 * @param random the random number generator instance to use for the salt
	 * @param newPassword the new password
	 * @throws NoSuchAlgorithmException if the configured hash algorithm is not supported
	 */
	public void updatePassword(
			LabVisionConfig config,
			SecureRandom random,
			String newPassword)
			throws NoSuchAlgorithmException {
		// generate salt
		byte[] newSalt = new byte[config.getSaltSize()];
		random.nextBytes(newSalt);
		
		// update the password salt and hash
		this.setPasswordDigestBytes(
				hashPassword(
				newPassword, 
				newSalt, 
				config.getPasswordHashAlgorithm()
				));
		this.setPasswordSaltBytes(newSalt);
		
		this.setHashAlgorithm(config.getPasswordHashAlgorithm());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device device) {
		devices.add(device);
		device.setUser(this);
	}
	
	public void removeDevice(Device device) {
		devices.remove(device);
		device.setUser(null);
	}
	
	public abstract UserRole getRole();
}
