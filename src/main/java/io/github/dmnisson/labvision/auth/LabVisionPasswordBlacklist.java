package io.github.dmnisson.labvision.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class LabVisionPasswordBlacklist {

	@Autowired
	private LabVisionAuthConfig labVisionAuthConfig;
	
	private Set<String> blacklistedPasswords = new HashSet<>();
	
	@PostConstruct
	public void init() throws IOException {
		final Resource passwordBlacklistFile = labVisionAuthConfig.getPasswordBlacklistFile();
		
		if (Objects.nonNull(passwordBlacklistFile)) {
			final InputStream in = passwordBlacklistFile.getInputStream();
			
			final BufferedReader r = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
			
			String line;
			while ((line = r.readLine()) != null) {
				if (line.length() >= labVisionAuthConfig.getMinBlacklistedPasswordLength()) {
					blacklistedPasswords.add(line);
				}
			}
			
			r.close();
		}
	}
	
	public boolean isBlacklisted(String password, Collection<? extends String> additionalValues) {
		HashSet<String> localBlacklistedPasswords = new HashSet<>(blacklistedPasswords);
		localBlacklistedPasswords.addAll(additionalValues.stream()
				.filter(Objects::nonNull)
				.filter(value -> value.length() >= labVisionAuthConfig.getMinBlacklistedPasswordLength())
				.collect(Collectors.toSet())
				);
		
		return localBlacklistedPasswords.stream()
				.anyMatch(listedPassword -> password.toUpperCase().contains(listedPassword.toUpperCase()));
	}
}
