package io.github.dmnisson.labvision.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PaginationUtils {
	
	/**
	 * Adds model attributes for pagination of tables
	 * @param <T> the type of object to paginate
	 * @param model the model to which to add attributes
	 * @param page the page result from the database query
	 * @param qualifier the qualifier for the table
	 * @param controllerClass the controller class
	 * @param methodName the name of the controller method
	 * @param args the controller method arguments; may be null if not needed for URL
	 * generation
	 */
	public static <T> void addPageModelAttributes(Model model, Page<T> page, String qualifier, Class<?> controllerClass, String methodName, Object... args) {
		List<Integer> pages = getPageNumbersList(page);
		
		String qualifierPrefix = StringUtils.isEmpty(qualifier) ? "" : qualifier + "_";
		
		model.addAttribute(qualifierPrefix + "pages", pages);
		model.addAttribute(qualifierPrefix + "currentPage", page.getNumber() + 1);
		if (page.getNumber() > 0) {
			model.addAttribute(
					qualifierPrefix + "prevPageUrl", 
					buildPageUrl(
							page,
							page.getNumber() - 1,
							qualifier,
							qualifierPrefix,
							controllerClass, 
							methodName,
							args
							)
					);
		}
		if (page.getNumber() < page.getTotalPages() - 1) {
			model.addAttribute(
					qualifierPrefix + "nextPageUrl", 
					buildPageUrl(
							page,
							page.getNumber() + 1,
							qualifier,
							qualifierPrefix,
							controllerClass, 
							methodName,
							args
							)
					);
		}
		
		Map<Integer, String> pageUrls = pages.stream()
				.collect(Collectors.toMap(
						Function.identity(), 
						p -> buildPageUrl(
								page,
								p - 1,
								qualifier,
								qualifierPrefix,
								controllerClass, 
								methodName,
								args
								)
						));
		model.addAttribute(qualifierPrefix + "pageUrls", pageUrls);
	}
	
	/**
	 * Gets the list of all of the page numbers of the data set
	 * @param <T> the type of paginated result
	 * @param page the page
	 * @return the list of page numbers
	 */
	private static <T> List<Integer> getPageNumbersList(Page<T> page) {
		return IntStream.range(1, page.getTotalPages() + 1)
						.mapToObj(Integer::valueOf)
						.collect(Collectors.toList());
	}
	
	/**
	 * Adds the appropriate model attributes for page navigation of pages for tables mapped by a key
	 * @param <K> the key type
	 * @param <T> the value type
	 * @param model the model
	 * @param pages the pages
	 * @param mapName the name of the map in the URL
	 * @param mapKeyDelimiter the delimiter to separate the map name from the key
	 * @param controllerClass the controller class
	 * @param methodName the method name
	 * @param args the method arguments needed to build URLs
	 */
	public static <K, T> void addMappedPageModelAttributes(Model model,
			Map<K, Page<T>> pages, String mapName,
			String mapKeyDelimiter, Class<?> controllerClass, String methodName, Object... args) {
		
		String modelAttributePrefix = mapName + "_";
		
		Map<K, List<Integer>> pagesMap = new HashMap<>();
		Map<K, Integer> currentPageMap = new HashMap<>();
		Map<K, String> prevPageUrlMap = new HashMap<>();
		Map<K, String> nextPageUrlMap = new HashMap<>();
		Map<K, Map<Integer, String>> pageUrlsMap = new HashMap<>();
		
		pages.forEach((key, page) -> {
			List<Integer> pageNumbers = getPageNumbersList(page);
			
			String qualifier = mapName + mapKeyDelimiter + key;
			String urlParameterPrefix = mapName + mapKeyDelimiter + key + "_";
			
			pagesMap.put(key, pageNumbers);
			currentPageMap.put(key, page.getNumber() + 1);
			if (page.getNumber() > 0) {
				prevPageUrlMap.put(key,
						buildPageUrl(
							page,
							page.getNumber() - 1,
							qualifier,
							urlParameterPrefix,
							controllerClass,
							methodName,
							args
						)
						);
			}
			if (page.getNumber() < page.getTotalPages() - 1) {
				nextPageUrlMap.put(key,
						buildPageUrl(
							page,
							page.getNumber() + 1,
							qualifier,
							urlParameterPrefix,
							controllerClass,
							methodName,
							args
						)
						);
			}
			pageUrlsMap.put(key,
					pagesMap.get(key).stream()
						.collect(Collectors.toMap(
								Function.identity(),
								p -> buildPageUrl(
									page,
									p - 1,
									qualifier,
									urlParameterPrefix,
									controllerClass,
									methodName,
									args
								)
						))
					);
		});
		
		model.addAttribute(modelAttributePrefix + "pages", pagesMap);
		model.addAttribute(modelAttributePrefix + "currentPage", currentPageMap);
		model.addAttribute(modelAttributePrefix + "prevPageUrl", prevPageUrlMap);
		model.addAttribute(modelAttributePrefix + "nextPageUrl", nextPageUrlMap);
		model.addAttribute(modelAttributePrefix + "pageUrls", pageUrlsMap);
		
	}

	// Helper function that builds the appropriate URL for the page
	private static <T> String buildPageUrl(Page<T> page, final int pageNumber, String qualifier,
			String qualifierPrefix, Class<?> controllerClass, String methodName, Object... args) {
		final UriComponentsBuilder uriComponentsBuilder = MvcUriComponentsBuilder.fromMethodName(
				controllerClass,
				methodName,
				args
				)
		.queryParam(qualifierPrefix + "page", pageNumber)
		.queryParam(qualifierPrefix + "size", page.getSize());
		
		if (StringUtils.hasLength(qualifier)) {
			uriComponentsBuilder.queryParam("activePane", qualifier);
		}
		return uriComponentsBuilder.build().toUriString();
	}

}
