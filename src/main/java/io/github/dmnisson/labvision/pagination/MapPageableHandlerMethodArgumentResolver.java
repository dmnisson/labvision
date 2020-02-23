package io.github.dmnisson.labvision.pagination;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.github.dmnisson.labvision.utils.Pair;

/**
 * Supports method parameters of types like {@code Map<T, Pageable>} for pages with multiple
 * tables keyed by an object of type {@code T}, such as an identifier for an owning JPA entity
 * @author David Nisson
 *
 */
public class MapPageableHandlerMethodArgumentResolver 
	extends PageableHandlerMethodArgumentResolverSupport
	implements HandlerMethodArgumentResolver {

	private final String DEFAULT_MAP_KEY_DELIMITER = ".";
	private final String DEFAULT_MAP_PAGE_PARAMETER_DELIMITER = "_";
	
	private final Pageable DEFAULT_PAGE_REQUEST_FOR_MAP = PageRequest.of(0, 20);
	
	private String mapKeyDelimiter = DEFAULT_MAP_KEY_DELIMITER;
	private String mapPageParameterDelimiter = DEFAULT_MAP_PAGE_PARAMETER_DELIMITER;
	
	private Pageable defaultPageableForMap = DEFAULT_PAGE_REQUEST_FOR_MAP;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.getGenericParameterType() instanceof ParameterizedType) {
			ParameterizedType parameterType = (ParameterizedType) parameter.getGenericParameterType();
			
			if (Map.class.equals(parameterType.getRawType())) {
				Type[] typeArgs = parameterType.getActualTypeArguments();
				if (Pageable.class.equals(typeArgs[1])) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			@Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			@Nullable WebDataBinderFactory binderFactory) {

		assertMapPageableUniqueness(methodParameter);
		
		String mapName = getMapName(methodParameter);
		
		Pattern parameterNamePattern = Pattern.compile(
				"^" + mapName + "\\Q" + mapKeyDelimiter + "\\E(.+)\\Q" + mapPageParameterDelimiter + "\\E(\\w+)$");
		
		// key -> page parameters
	    Map<String, Map<String, String>> keyPageParameters = StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(webRequest.getParameterNames(), Spliterator.ORDERED),
				false)
			.map(parameterName -> parameterNamePattern.matcher(parameterName))
			.map(matcher -> {
				if (!matcher.matches()) return null;
				
				String key = matcher.group(1);
				
				return new Pair<>(key, matcher);
			}).filter(Objects::nonNull)
			// collect page parameter names and values for each map name into a nested map
			// cannot use Collectors.groupingBy with Collectors.toMap because of issues with null values
			.collect(HashMap::new,
					(map, pair) -> {
						if (!map.containsKey(pair.getKey())) {
							map.put(pair.getKey(), new HashMap<>());
						}
						
						map.get(pair.getKey()).put(
								pair.getValue().group(2), 
								webRequest.getParameter(pair.getValue().group())
								);
					},
					HashMap::putAll
					);
	    
		Type keyType = ((ParameterizedType) methodParameter.getGenericParameterType())
				.getActualTypeArguments()[0];
		
		return keyPageParameters.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> parseKey(keyType, entry.getKey()),
						entry -> getPageable(entry)
						));
	}

	/**
	 * Gets the qualifying name of the Map<T, Pageable> in the given method parameter, which is
	 * used to distinguish it in the query string
	 * @param methodParameter the method parameter
	 * @return the name of the map
	 */
	private String getMapName(MethodParameter methodParameter) {
		Qualifier qualifier = methodParameter.getParameterAnnotation(Qualifier.class);
		
		String mapName = Objects.isNull(qualifier) ? methodParameter.getParameterName() : qualifier.value();
		
		Assert.doesNotContain(mapName, mapKeyDelimiter, "Map name cannot contain the key delimiter");
		
		return mapName;
	}

	/**
	 * Asserts that all map names are unique.
	 * @param methodParameter
	 */
	private static void assertMapPageableUniqueness(MethodParameter methodParameter) {
		
		Method method = methodParameter.getMethod();
		
		if (Objects.isNull(method)) {
			throw new IllegalArgumentException(String.format("Method parameter %s is not backed by a method.", methodParameter));
		}
		
		Set<String> mapNames = new HashSet<>();
		
		Annotation[][] annotations = method.getParameterAnnotations();
		Type[] types = method.getGenericParameterTypes();
		Parameter[] parameters = method.getParameters();
		
		for (int i = 0; i < annotations.length; i++) {
			if (types[i] instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) types[i];
				
				if (Pageable.class.equals(parameterizedType.getActualTypeArguments()[1])) {
					
					Optional<Qualifier> qualifier = Stream.of(annotations[i])
							.filter(a -> a instanceof Qualifier)
							.map(a -> (Qualifier) a)
							.findAny();
					
					final String mapName = qualifier.isPresent() ? qualifier.get().value() : parameters[i].getName();
					
					if (mapNames.contains(mapName)) {
						throw new IllegalArgumentException("Map names must be unique!");
					}
					
					mapNames.add(mapName);
				}
			}
		}
	}

	private static Object parseKey(Type keyType, String keyString) {
		if (keyType instanceof Class) {
			Class<?> keyClass = (Class<?>) keyType;
			
			try {
				return keyClass.getConstructor(String.class).newInstance(keyString);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
				throw new IllegalArgumentException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			}
			
		} else {
			throw new IllegalArgumentException("pages must not be keyed by generic types");
		}
	}

	private PageRequest getPageable(Entry<String, Map<String, String>> keyParameterMapEntry) {
		// adaptation of
		// org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport.getPageable()
		
		Map<String, String> pageParameters = keyParameterMapEntry.getValue();
		
		String pageString = pageParameters.get(getPageParameterName());
		String pageSizeString = pageParameters.get(getSizeParameterName());
		
		Optional<Integer> page = parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true);
		Optional<Integer> pageSize = parseAndApplyBoundaries(pageSizeString, getMaxPageSize(), false);
		
		int p = page.orElseGet(() -> defaultPageableForMap.getPageNumber());
		int ps = pageSize.orElseGet(() -> defaultPageableForMap.getPageSize());
		
		// Limit lower bound
		ps = ps < 1 ? defaultPageableForMap.getPageSize() : ps;
		// Limit upper bound
		ps = ps > getMaxPageSize() ? getMaxPageSize() : ps;
		
		return PageRequest.of(p, ps, defaultPageableForMap.getSort());
	}

	public String getMapKeyDelimiter() {
		return mapKeyDelimiter;
	}

	public void setMapKeyDelimiter(String mapKeyDelimiter) {
		this.mapKeyDelimiter = mapKeyDelimiter;
	}
	
	// from org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport
	private Optional<Integer> parseAndApplyBoundaries(@Nullable String parameter, int upper, boolean shiftIndex) {

		if (!StringUtils.hasText(parameter)) {
			return Optional.empty();
		}

		try {
			int parsed = Integer.parseInt(parameter) - (isOneIndexedParameters() && shiftIndex ? 1 : 0);
			return Optional.of(parsed < 0 ? 0 : parsed > upper ? upper : parsed);
		} catch (NumberFormatException e) {
			return Optional.of(0);
		}
	}

	public void setDefaultPageableForMap(Pageable defaultPageableForMap) {
		Assert.notNull(defaultPageableForMap, "Default Pageable must not be null");
		
		this.defaultPageableForMap = defaultPageableForMap;
	}
}
