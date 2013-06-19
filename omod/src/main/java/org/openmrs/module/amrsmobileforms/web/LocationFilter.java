package org.openmrs.module.amrsmobileforms.web;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet filter to return the location of syncing server
 * 
 * @author Samuel Mbugua
 *
 */
public final class LocationFilter implements Filter {

    @SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse hrs = null;
		hrs = (HttpServletResponse)response;
		hrs.setHeader("Location", "openmrs/moduleServlet/amrsmobileforms/fileUpload");
		chain.doFilter(request, hrs);
    }
    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
    }

}


