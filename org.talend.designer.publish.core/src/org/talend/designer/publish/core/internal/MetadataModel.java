package org.talend.designer.publish.core.internal;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MetadataModel {

	private Document document;
	private Element root;

	public MetadataModel(String groupId, String artifactId)
			throws ParserConfigurationException {
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.newDocument();
		initialDocument(groupId, artifactId);
	}

	public MetadataModel(String uri) throws SAXException, IOException,
			ParserConfigurationException {
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(uri);
		root = document.getDocumentElement();
	}

	private void initialDocument(String groupId, String artifactId) {
		root = document.createElement("metadata");
		document.appendChild(root);

		Element groupIdElement = document.createElement("groupId");
		root.appendChild(groupIdElement);
		groupIdElement.appendChild(document.createTextNode(groupId));

		Element artifactIdElement = document.createElement("artifactId");
		root.appendChild(artifactIdElement);
		artifactIdElement.appendChild(document.createTextNode(artifactId));

	}

	public void addVersion(String version) {
		Element versioningElement = null;
		NodeList elements = root.getElementsByTagName("versioning");
		if (elements == null || elements.getLength() == 0) {
			versioningElement = document.createElement("versioning");
			root.appendChild(versioningElement);
		} else {
			versioningElement = (Element) elements.item(0);
		}

		Element releaseElement = null;
		elements = versioningElement.getElementsByTagName("release");
		if (elements == null || elements.getLength() == 0) {
			releaseElement = document.createElement("release");
			releaseElement.setTextContent(version);
			versioningElement.appendChild(releaseElement);
		} else {
			releaseElement = (Element) elements.item(0);
			releaseElement.setTextContent(version);
		}

		Element versionsElement = null;
		elements = versioningElement.getElementsByTagName("versions");
		if (elements == null || elements.getLength() == 0) {
			versionsElement = document.createElement("versions");
			versioningElement.appendChild(versionsElement);
		} else {
			versionsElement = (Element) elements.item(0);
		}
		Element versionElement = document.createElement("version");
		versionElement.setTextContent(version);
		versionsElement.appendChild(versionElement);

		Element lastUpdatedElement = null;
		elements = versioningElement.getElementsByTagName("lastUpdated");
		if (elements == null || elements.getLength() == 0) {
			lastUpdatedElement = document.createElement("lastUpdated");
			lastUpdatedElement.setTextContent(getLastUpdatedTimestamp());
			versioningElement.appendChild(lastUpdatedElement);
		} else {
			lastUpdatedElement = (Element) elements.item(0);
			lastUpdatedElement.setTextContent(getLastUpdatedTimestamp());
		}
	}

	private String getLastUpdatedTimestamp() {
		Date lastUpdatedTimestamp = Calendar.getInstance().getTime();
		TimeZone timezone = TimeZone.getTimeZone("UTC");
		DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		fmt.setTimeZone(timezone);
		String timestamp = fmt.format(lastUpdatedTimestamp);
		return timestamp;
	}

	public String getNewContent(){
		return toString();
	}
	
	@Override
	public String toString() {
		return saveDocument();
	}

	private String saveDocument() {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", new Integer(4));
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					bao));
			transformer.transform(new DOMSource(document), new StreamResult(
					writer));
			writer.close();
			return bao.toString();
		} catch (Exception e) {
		}
		return "";
	}

	//for test
//	public static void main(String[] args) throws ParserConfigurationException,
//			TransformerException, IOException {
//		MetadataModel metadataModel = new MetadataModel("trg.talend.liugang",
//				"TestEERoute");
//		metadataModel.addVersion("1.0.45");
//		metadataModel.addVersion("1.0.46");
//		System.out.println(metadataModel.saveDocument());
//	}
}