package com.b50.migrations;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MigrationsResourceFileParser {

	private Document document;
	
	public MigrationsResourceFileParser(String path) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		this.document = builder.parse(path);
	}
		
	public int getNextSequence() throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("/resources/integer[@name='database_version']/text()");
		String result = (String)expr.evaluate(this.document, XPathConstants.STRING);
		return Integer.valueOf(result);
	}

	public String getPackageName() throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("/resources/string[@name='package_name']/text()");
		String result = (String)expr.evaluate(this.document, XPathConstants.STRING);
		return result;
	}

	public String getDatabaseName() throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("/resources/string[@name='database_name']/text()");
		String result = (String)expr.evaluate(this.document, XPathConstants.STRING);
		return result;
	}
}
