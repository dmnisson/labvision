package labvision.entities;

import java.util.stream.Stream;

public enum FileType {
	PDF("application/pdf"),
	WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	WORD_COMPAT("application/msword");

	private final String contentType;
	
	FileType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public static FileType fromFilename(String filename) {
		return ofExtension(
				filename.substring(filename.lastIndexOf('.') + 1)
				);
	}

	public static FileType ofContentType(String contentType) {
		return Stream.of(FileType.values())
				.filter(type -> type.getContentType().equals(contentType))
				.findAny().orElse(null);
	}
	
	public static FileType ofExtension(String extension) {
		switch (extension) {
		case "doc":
			return WORD_COMPAT;
		case "docx":
			return WORD;
		case "pdf":
			return PDF;
		default:
			return null;
		}
	}
}
