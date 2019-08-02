package labvision.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import labvision.utils.ByteArrayStringConverter;

@Entity
public abstract class User {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	protected int id;
	
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
	public void setPasswordSalt(byte[] passwordSalt) {
		this.passwordSalt = ByteArrayStringConverter.toHexString(passwordSalt);
	}
}
