package labvision.utils;

public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T input) throws E;
}
