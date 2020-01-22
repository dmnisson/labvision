package labvision.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import labvision.LabVisionConfig;

public class DeviceTokenSigning {
	private final LabVisionConfig config;
	
	public DeviceTokenSigning(LabVisionConfig config) {
		this.config = config;
	}
	
	public byte[] getSignature(byte[] unsignedToken) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
		// load the keystore
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(
			new FileInputStream(config.getDeviceTokenKeystoreFilename()),
			config.getDeviceTokenKeystorePassword().toCharArray()
		);
		
		PrivateKey privateKey = (PrivateKey) keyStore.getKey("devauth",
				config.getDeviceTokenKeystorePassword().toCharArray());
		
		// generate the signature
		Signature signature = Signature.getInstance(config.getDeviceTokenSignatureAlgorithm());
		signature.initSign(privateKey);
		signature.update(unsignedToken);
		
		return signature.sign();
	}
}
