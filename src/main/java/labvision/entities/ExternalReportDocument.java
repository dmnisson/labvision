package labvision.entities;

import java.net.MalformedURLException;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.URL;
import org.jboss.logging.Logger;

/**
 * A report document stored on an external service
 * @author davidnisson
 *
 */
@Entity( name = "ExternalReportDocument" )
public class ExternalReportDocument extends ReportDocument {
	@URL
	private String reportDocumentURLString;
	
	@Override
	public java.net.URL getReportDocumentURL(java.net.URL context) {
		try {
			return new java.net.URL(getReportDocumentURLString());
		} catch (MalformedURLException e) {
			Logger.getLogger(this.getClass()).error("Invalid URL in external report document", e);
			throw new RuntimeException(e);
		}
	}

	public String getReportDocumentURLString() {
		return reportDocumentURLString;
	}

	public void setReportDocumentURLString(String reportDocumentURLString) {
		this.reportDocumentURLString = reportDocumentURLString;
	}
}
