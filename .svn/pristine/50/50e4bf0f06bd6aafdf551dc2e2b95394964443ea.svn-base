package com.littlecloud.control.webservices;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

//@WebFilter(filterName = "filteranno", urlPatterns = "/*",
//		initParams = { @WebInitParam(name = "param1", value = "RoseIndia") })
public class RequestFilter implements Filter {
	
	private static final Logger log = Logger.getLogger(RequestFilter.class);
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
	{

		HttpServletRequest request = (HttpServletRequest) req;

		// Get the IP address of client machine.
		String ipAddress = request.getRemoteAddr();

		// Log the IP address and current timestamp.
		log.info("IP " + ipAddress + ", Time "
				+ new Date().toString());

		chain.doFilter(req, res);
	}

	public void init(FilterConfig config) throws ServletException {

		// Get init parameter
		String testParam = config.getInitParameter("param1");

		// Print the init parameter
		log.info("Init Param Name : " + testParam);
	}

	public void destroy() {
		// add code to release any resource
	}
}