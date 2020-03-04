package io.github.dmnisson.labvision.utils;

import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

public class NullSafeNumberUtils {
	
	public static <T extends Number> Optional<T> parseNumberOptional(@Nullable String text, Class<T> targetClass) {
		Objects.requireNonNull(targetClass, "Target class must not be null!");
		
		if (StringUtils.hasLength(text)) {
			return Optional.of(NumberUtils.parseNumber(text, targetClass));
		} else {
			return Optional.empty();
		}
	}
	
}
