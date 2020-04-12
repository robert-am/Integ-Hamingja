package com.tramisalud;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlAnexos1Reader {

	private DocumentBuilderFactory DocBFactory = null;
	Document document = null;
	NodeList nodeList = null;
	Map<String, Object> keyValue = null;;
	String parent = "";
	Integer iNode = 0;
	
	public XmlAnexos1Reader(String uri) {
		DocBFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder DocBuilder;
		try {
			DocBuilder = DocBFactory.newDocumentBuilder();

			File file = new File(uri);
			InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream,
					Charset.forName("ISO-8859-1"));

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
				if(parent.equals("Inconsistencias")){
					iNode++;
				}
				parseXMLtoMap(node.getChildNodes().item(0));
			} else {
				if (keyValue.containsKey(node.getNodeName())) {
					if(parent.equals("Inconsistencias")){
						keyValue.put(node.getNodeName() + iNode.toString(),
							node.getTextContent());
					} else {
						keyValue.put(parent + node.getNodeName(),
							node.getTextContent());
					}
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
	}
}
