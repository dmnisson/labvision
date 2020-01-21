package labvision;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import labvision.utils.ByteArrayStringConverter;

/**
 * Servlet Filter implementation class GenerateCsrfSalt
 */
@WebFilter("/GenerateCsrfSalt")
public class GenerateCsrfSalt implements Filter {

	private FilterConfig fConfig;
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		LabVisionConfig config = (LabVisionConfig) fConfig.getServletContext()
				.getAttribute(LabVisionServletContextListener.CONFIG_ATTR);
		
		// Throw exception if not HTTP
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		// Check to see if there is a salt cache for the user session
		@SuppressWarnings("unchecked")
		Cache<String, Boolean> saltCache = (Cache<String, Boolean>)
				httpRequest.getSession().getAttribute("csrfSaltCache");
		
		if (Objects.isNull(saltCache)) {
			saltCache = CacheBuilder.newBuilder()
					.maximumSize(config.getMaxCsrfSaltCacheSize())
					.expireAfterWrite(config.getCsrfSaltExpirationTime(), TimeUnit.SECONDS)
					.build();
			
			httpRequest.getSession().setAttribute("csrfSaltCache", saltCache);
		}

		// generate the salt and cache it
		byte[] saltBytes = new byte[config.getSaltSize()];
		SecureRandom random;
		try {
			random = SecureRandom.getInstance(config.getCsrfSaltAlgorithm());
			random.nextBytes(saltBytes);
			String salt = ByteArrayStringConverter.toHexString(saltBytes);
			saltCache.put(salt, true);
			
			// add the salt to the current request
			httpRequest.setAttribute("csrfSalt", salt);
			
			// pass the request along the filter chain
			chain.doFilter(request, response);
		} catch (NoSuchAlgorithmException e) {
			((HttpServletResponse) response).sendError(500, e.getMessage());
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.fConfig = fConfig;
	}

}
