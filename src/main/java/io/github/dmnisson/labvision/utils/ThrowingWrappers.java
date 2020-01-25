package io.github.dmnisson.labvision.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThrowingWrappers {
	/**
	 * Adapts a ThrowingFunction to a standard Java API that accepts only Functions
	 * @param throwingFunction the function throwing the exception
	 * @return a function that encapsulates the throwing function. 
	 * This function throws a RuntimeException when the encapsulated function throws
	 * an Exception.
	 */
	public static <T, R> Function<T, R> throwingFunctionWrapper(ThrowingFunction<T, R, Exception> throwingFunction) {
		return a -> {
			try {
				return throwingFunction.apply(a);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Creates a mapping of collection values to results of a
	 * function to be applied to each element of the collection.
	 * @param <K> the type of element in the collection
	 * @param <R> the type of result
	 * @param collection the collection to be mapped
	 * @param throwingFunction the function to be applied
	 * @return a map of distinct collection values to function results
	 */
	public static <K, R> Map<K, R> collectionToMap(Collection<? extends K> collection, ThrowingFunction<K, R, Exception> throwingFunction) {
		return collection.stream().distinct()
				.collect(Collectors.toMap(
						Function.identity(),
						throwingFunctionWrapper(key -> throwingFunction.apply(key))
						));
	}
}
