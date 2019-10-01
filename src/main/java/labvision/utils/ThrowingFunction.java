package labvision.utils;

/**
 * Function that throws an exception
 * @author davidnisson
 *
 * @param <T> the type of parameter
 * @param <R> the return type
 * @param <E> the exception type
 */
public interface ThrowingFunction<T, R, E extends Exception> {
	R apply(T t) throws E;
}
