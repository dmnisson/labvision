package io.github.dmnisson.labvision.utils;

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
		List<Integer> pages = IntStream.range(1, page.getTotalPages() + 1)
				.mapToObj(Integer::valueOf)
				.collect(Collectors.toList());
		
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
