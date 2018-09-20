package com.littlecloud.pool.utils;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlParser {

	private static final Logger log = Logger.getLogger(XmlParser.class);

	public static String getXmlPath(String filePath, String xmlPath) {
		String result = null;
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error(e, e);
		}

		try {
			Document xmldoc = builder.parse(new FileInputStream(filePath));
			XPath xPath = XPathFactory.newInstance().newXPath();
			result = xPath.compile(xmlPath).evaluate(xmldoc);

		} catch (SAXException | IOException e) {
			log.error(e, e);
		} catch (XPathExpressionException e) {
			log.error(e, e);
		}
		
		return result;
	}
}
