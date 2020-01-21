package labvision;

import java.io.IOException;
import java.util.Objects;

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

/**
 * Servlet Filter implementation class ValidateCsrfSalt
 */
@WebFilter("/ValidateCsrfSalt")
public class ValidateCsrfSalt implements Filter {

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// throw error if this is not an HTTP request
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// get the CSRF salt sent with the request
		String salt = httpRequest.getParameter("csrfSalt");
		
		if (httpRequest.getMethod().equalsIgnoreCase("GET")) {
			// we're okay -- early exit
			chain.doFilter(request, response);
			return;
		}
		
		// validate salt in cache
		@SuppressWarnings("unchecked")
		Cache<String, Boolean> saltCache = (Cache<String, Boolean>)
				httpRequest.getSession().getAttribute("csrfSaltCache");
		
		if (Objects.nonNull(saltCache) 
				&& Objects.nonNull(salt) 
				&& Objects.nonNull(saltCache.getIfPresent(salt))) {
			chain.doFilter(request, response);
		} else {
			httpResponse.sendError(422, "CSRF salt invalid");
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
