package labvision.utils;

import java.util.function.Function;

public class ThrowingWrappers {
	public static <T, R> Function<T, R> throwingFunctionWrapper(ThrowingFunction<T, R, Exception> throwingFunction) {
		return a -> {
			try {
				return throwingFunction.apply(a);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
