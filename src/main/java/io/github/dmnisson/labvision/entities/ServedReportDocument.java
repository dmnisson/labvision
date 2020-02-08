package io.github.dmnisson.labvision.entities;

import java.net.MalformedURLException;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.jboss.logging.Logger;

/**
 * A report document served by the LabVision server
 * @author davidnisson
 *
 */
@Entity( name="ServedReportDocument" )
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class ServedReportDocument extends ReportDocument {
	
	@Column
	/**
	 * The information that needs to be passed to the document URL
	 * to obtain this document
	 */ 
	protected String docsPathInfo;

	public String getDocsPathInfo() {
		return docsPathInfo;
	}

	public void setDocsPathInfo(String docsPathInfo) {
		this.docsPathInfo = docsPathInfo;
	}
	
	@Override
	public URL getReportDocumentURL(URL servletURL) {
		try {
			return new URL(servletURL.toString() + "/" + docsPathInfo);
		} catch (MalformedURLException e) {
			Logger.getLogger(this.getClass()).error("Invalid document URL", e);
			return null;
		}
	}
}
