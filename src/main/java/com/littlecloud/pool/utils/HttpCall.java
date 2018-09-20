package com.littlecloud.pool.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.logging.Logger;

public class HttpCall {

	private static final Logger log = Logger.getLogger(HttpCall.class);

	private HttpClient httpClient = new HttpClient();
	private String url = null;
	private NameValuePair[] param = null;
	private int timeoutInMillisec;

	public HttpCall(String url, NameValuePair[] param) {
		super();
		this.url = url;
		this.param = param;
		this.timeoutInMillisec = 5000;

		httpClient.setConnectionTimeout(timeoutInMillisec);
		httpClient.setTimeout(timeoutInMillisec);
	}
	
	public HttpCall(String url, NameValuePair[] param, int timeoutInMillisec) {
		super();
		this.url = url;
		this.param = param;
		this.timeoutInMillisec = timeoutInMillisec;

		httpClient.setConnectionTimeout(timeoutInMillisec);
		httpClient.setTimeout(timeoutInMillisec);
	}
	
	public String getText() {

		String ret = "";

		GetMethod getMethod = new GetMethod(url);
		if (param!=null)
			getMethod.setQueryString(param);
		getMethod.addRequestHeader("Content-Type", "application/text; charset=UTF-8");
		
		log.debugf("QueryString=%s", getMethod.getQueryString());

		try {
			httpClient.executeMethod(getMethod);

			if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
				try 
				{
					InputStream is = getMethod.getResponseBodyAsStream();
					if(is != null)
					{
						int ch = is.read();
						while(ch != -1)
						{
							ret += (char)ch;
							ch = is.read();
						}
					}
				} 
				catch (IOException e) 
				{
					log.error("HttpClient get response error " + e);
				}
			} else {
				log.error("get template return non-200 " + getMethod.getStatusCode() + " response body: " + getMethod.getResponseBodyAsString());
			}

		} catch (HttpException e) {
			log.errorf("error HttpCall HttpException on url %s with timeout %d, %s", url, timeoutInMillisec, e.getMessage());
			return null;
		} catch (IOException e) {
			log.errorf("error HttpCall IOException on url %s with timeout %d, %s", url, timeoutInMillisec, e.getMessage());
			return null;
		}

		if (log.isDebugEnabled()) log.debug(" HttpCall with result = " + ret);
		return ret;
	}

	public String getJson() {

		String ret = "";

		GetMethod getMethod = new GetMethod(url);
		if (param!=null)
			getMethod.setQueryString(param);
		getMethod.addRequestHeader("Content-Type", "application/json; charset=UTF-8");
		
		if (log.isDebugEnabled()) log.debugf("QueryString=%s", getMethod.getQueryString());

		try {
			httpClient.executeMethod(getMethod);

			if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
//				ret = getMethod.getResponseBodyAsString();
				try 
				{
					InputStream is = getMethod.getResponseBodyAsStream();
					if(is != null)
					{
						int ch = is.read();
						while(ch != -1)
						{
							ret += (char)ch;
							ch = is.read();
						}
					}
				} 
				catch (IOException e) 
				{
					log.error("HttpClient get response error " + e);
				}
//				log.debug("response body: " + getMethod.getResponseBodyAsString());
			} else {
				log.error("get template return non-200 " + getMethod.getStatusCode() + " response body: " + getMethod.getResponseBodyAsString());
			}

		} catch (HttpException e) {
			log.errorf("error HttpCall HttpException on url %s with timeout %d", url, timeoutInMillisec, e.getMessage());
			return null;
		} catch (IOException e) {
			log.errorf("error HttpCall IOException on url %s with timeout %d", url, timeoutInMillisec, e.getMessage());
			return null;
		}

		log.debug(" HttpCall with result = " + ret);
		return ret;
	}

	// untest
	public String postJson() {

		String ret = null;

		PostMethod postMethod = new PostMethod(url);
		if (param!=null)
			postMethod.setQueryString(param);
		postMethod.addRequestHeader("Content-Type", "application/json; charset=UTF-8");

		try {
			httpClient.executeMethod(postMethod);

			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				try 
				{
					InputStream is = postMethod.getResponseBodyAsStream();
					if(is != null)
					{
						int ch = is.read();
						while(ch != -1)
						{
							ret += (char)ch;
							ch = is.read();
						}
					}
				} 
				catch (IOException e) 
				{
					log.error("HttpClient get response error " + e);
				}
//				ret = postMethod.getResponseBodyAsString();
//				log.debug("response body: " + postMethod.getResponseBodyAsString());
			} else {
				log.debug("get template return non-200 " + postMethod.getStatusCode());
				log.debug("response body: " + postMethod.getResponseBodyAsString());
			}
		} catch (HttpException e) {
			log.errorf("error requesting email template on url %s with timeout %d", url, timeoutInMillisec, e.getMessage());
			return null;
		} catch (IOException e) {
			log.errorf("error requesting email template on url %s with timeout %d", url, timeoutInMillisec, e.getMessage());
			return null;
		}
		
		log.debug(" get template with result = " + ret);
		return ret;
	}
}
