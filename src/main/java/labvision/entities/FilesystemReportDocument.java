package labvision.entities;

import java.io.File;

import javax.persistence.Entity;

@Entity( name="FilesystemReportDocument" )
public class FilesystemReportDocument extends ServedReportDocument {
	/**
	 * The path of the document on the file system
	 */
	private String filesystemPath;

	{
		setDocumentType(ReportDocumentType.FILESYSTEM);
	}
	
	public File getReportDocumentFile() {
		return new File(getFilesystemPath());
	}

	public String getFilesystemPath() {
		return filesystemPath;
	}

	public void setFilesystemPath(String filesystemPath) {
		this.filesystemPath = filesystemPath;
	}
}
