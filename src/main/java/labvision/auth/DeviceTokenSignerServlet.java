package labvision.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import labvision.LabVisionServletContextListener;

/**
 * Servlet for signing device tokens when no external token signer is available
 */
public class DeviceTokenSignerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DeviceTokenSigning signing = (DeviceTokenSigning) getServletContext()
				.getAttribute(LabVisionServletContextListener.DEVICE_TOKEN_SIGNING_ATTR);
		
		String action = request.getPathInfo();
		if (action == null) {
			response.sendError(400, "Must specify an action");
		}
		
		// remove leading slash
		action = action.substring(1);
		
		if (!action.equals("sign")) {
			response.sendError(500, "Unrecognized action: " + action);
			return;
		}
		
		DataInputStream in = new DataInputStream(request.getInputStream());
		String unsignedTokenString = in.readUTF();
		
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] unsignedTokenBytes = decoder.decode(unsignedTokenString);
		
		try {
			byte[] signatureBytes = signing.getSignature(unsignedTokenBytes);
			
			Base64.Encoder encoder = Base64.getEncoder();
			String signatureString = encoder.encodeToString(signatureBytes);
			
			DataOutputStream out = new DataOutputStream(response.getOutputStream());
			out.writeUTF(signatureString);
			out.close();
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnrecoverableKeyException | KeyStoreException | CertificateException e) {
			response.sendError(500, e.getMessage());
		}
	}

}
