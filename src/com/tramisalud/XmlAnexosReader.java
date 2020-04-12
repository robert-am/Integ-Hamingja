package com.tramisalud;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlAnexosReader {

	private DocumentBuilderFactory DocBFactory = null;
	Document document = null;
	NodeList nodeList = null;
	Map<String, Object> keyValue = null;;
	String parent = "";

	public XmlAnexosReader(String uri) {
		DocBFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder DocBuilder;
		try {
			DocBuilder = DocBFactory.newDocumentBuilder();

			File file = new File(uri);
			InputStream inputStream = new FileInputStream(file);
			
			
			//inputStream.read();
			//inputStream.read();
			//inputStream.read();
			
			
			BufferedReader br = new BufferedReader (new InputStreamReader (inputStream,
					Charset.forName("ISO-8859-1")));
			
			//Reader reader = new InputStreamReader(inputStream, Charset.forName("ISO-8859-1"));

			String lineReader =" ";
			StringBuffer buffer = new StringBuffer();
			
			while((lineReader = br.readLine()) != null){
			  lineReader = lineReader.replaceAll("[^\\x20-\\x7e]", "");
			  buffer.append(lineReader);
			 }
			
			 byte[] bytes = buffer.toString().getBytes();
             
            InputStream reader = new ByteArrayInputStream(bytes);
			
			InputSource is = new InputSource(reader);
			is.setEncoding("ISO-8859-1");
			
			document = DocBuilder.parse(is);

			// document = DocBuilder.parse(uri);
			nodeList = document.getDocumentElement().getChildNodes();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, Object> getMappingValues() {
		keyValue = new LinkedHashMap<String, Object>();
		parseXMLtoMap(nodeList.item(0));
		return keyValue;
	}

	private void parseXMLtoMap(Node node) {
		if (node instanceof Element) {
			if (node.hasChildNodes() && node.getChildNodes().getLength() > 1) {
				// System.out.println(node.getNodeName());
				parent = node.getNodeName();
				parseXMLtoMap(node.getChildNodes().item(0));
			} else {
				if (keyValue.containsKey(node.getNodeName())) {
					keyValue.put(parent + node.getNodeName(),
							node.getTextContent());
				} else {
					keyValue.put(node.getNodeName(), node.getTextContent());
				}
				// System.out.println(node.getNodeName() + ":" +
				// node.getTextContent());
			}
			if (node.getNextSibling() != null) {
				parseXMLtoMap(node.getNextSibling());
			} else {
			}
		} else {
			if (node.getNextSibling() != null) {
				parseXMLtoMap(node.getNextSibling());
			} else {
				return;
			}
		}
		//
	}
}
