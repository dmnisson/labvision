package io.github.dmnisson.labvision.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.admin.AdminController;

public class PaginationUtils {

	// adds model attributes for pagination
	public static <T> void addPageModelAttributes(Model model, Page<T> page, String methodName, Object... pathArgs) {
		List<Integer> pages = IntStream.range(1, page.getTotalPages() + 1)
				.mapToObj(Integer::valueOf)
				.collect(Collectors.toList());
		
		model.addAttribute("pages", pages);
		model.addAttribute("currentPage", page.getNumber() + 1);
		if (page.getNumber() > 0) {
			Object[] args = Stream.concat(
								Stream.of(pathArgs),
								Stream.of(
										new Object(), 
										new Object()
								)
							).toArray();
			model.addAttribute("prevPageUrl", 
					MvcUriComponentsBuilder.fromMethodName(
							AdminController.class,
							methodName,
							args
							)
					.queryParam("page", page.getNumber() - 1)
					.queryParam("size", page.getSize())
					.build()
					.toUriString()
					);
		}
		if (page.getNumber() < page.getTotalPages() - 1) {
			Object[] args = Stream.concat(
					Stream.of(pathArgs),
					Stream.of(
							new Object(), 
							new Object()
					)
				).toArray();
			model.addAttribute("nextPageUrl", 
					MvcUriComponentsBuilder.fromMethodName(
							AdminController.class,
							methodName, 
							args
							)
					.queryParam("page", page.getNumber() + 1)
					.queryParam("size", page.getSize())
					.build()
					.toUriString()
					);
		}
		
		Map<Integer, String> pageUrls = pages.stream()
				.collect(Collectors.toMap(
						Function.identity(), 
						p -> {
							Object[] args = Stream.concat(
									Stream.of(pathArgs),
									Stream.of(
											new Object(), 
											new Object()
									)
								).toArray();
							return MvcUriComponentsBuilder.fromMethodName(
									AdminController.class,
									methodName,
									args
									)
							.queryParam("page", p - 1)
							.queryParam("size", page.getSize())
							.build()
							.toUriString();
						}
						));
		model.addAttribute("pageUrls", pageUrls);
	}

}
