package io.github.dmnisson.labvision.utils;

import java.net.URL;

public class URLUtils {

	public static String getFilenameFromURL(URL documentURL) {
		return documentURL.getPath()
							.substring(documentURL.getPath().lastIndexOf('/') + 1);
	}

}
