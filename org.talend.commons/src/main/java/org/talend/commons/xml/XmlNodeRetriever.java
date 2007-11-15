// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//   
// ============================================================================
package org.talend.commons.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.talend.commons.exception.ExceptionHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class XmlNodeRetriever {

    public static final String STRING_EMPTY = "";

    public static final String STRING_AT = "@";

    public static final String DEFAULT_PRE = "pre" + Math.random();

    private Document document;

    private XPath xpath;

    private String currentLoopXPath;

    private NamespaceContext namespaceContext;

    private final Map<String, String> prefixToNamespace = new HashMap<String, String>();

    /**
     * DOC amaumont XMLNodeRetriever constructor comment.
     * 
     * @param string
     */
    public XmlNodeRetriever(String filePath, String loopXPath) {
        super();
        this.currentLoopXPath = loopXPath;
        initNamespaceContext();
        initSource(filePath);

    }

    /**
     * DOC amaumont Comment method "initNamespaceContext".
     */
    private void initNamespaceContext() {
        namespaceContext = new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                String namespaceForPrefix = getNamespaceForPrefix(prefix);
                return namespaceForPrefix;
            }

            // Dummy implementation - not used!
            public Iterator getPrefixes(String val) {
                return null;
            }

            // Dummy implemenation - not used!
            public String getPrefix(String uri) {
                return null;
            }
        };

    }

    /**
     * DOC qzhang Comment method "getNamespaceForPrefix".
     * 
     * @param prefix
     * @return
     */
    protected String getNamespaceForPrefix(String prefix) {
        String namespace = prefixToNamespace.get(prefix);
        if (namespace != null) {
            return namespace;
        }
        return getDefaultNamespace();
    }

    /**
     * qzhang Comment method "getDefaultNamespace".
     * 
     * @return
     */
    private String getDefaultNamespace() {
        Node parent = document.getDocumentElement();
        int type = parent.getNodeType();
        if (type == Node.ELEMENT_NODE) {
            NamedNodeMap nnm = parent.getAttributes();
            for (int i = 0; i < nnm.getLength(); i++) {
                Node attr = nnm.item(i);
                String aname = attr.getNodeName();
                if (aname.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                    return attr.getNodeValue();
                }
            }
        }
        return XMLConstants.NULL_NS_URI;
    }

    /**
     * DOC amaumont Comment method "initSource".
     * 
     * @param filePath2
     */
    private synchronized void initSource(String filePath) {
        // Parse document containing schemas and validation roots
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(new File(filePath));
            prefixToNamespace.clear();
            initLastNodes(document.getDocumentElement());
            prefixToNamespace.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Create XPath factory for selecting schema and validation roots
        XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
        xpath.setNamespaceContext(namespaceContext);
    }

    /**
     * DOC qzhang Comment method "initLastNodes".
     * 
     * @param node
     */
    private void initLastNodes(Node node) {
        NodeList childNodes = node.getChildNodes();
        int length = childNodes.getLength();
        int type = node.getNodeType();
        if (type == Node.ELEMENT_NODE) {
            setPrefixToNamespace(node);
        }
        for (int i = 0; i < length; i++) {
            Node item = childNodes.item(i);
            if (item.getChildNodes().getLength() > 0) {
                initLastNodes(item);
            }
        }
    }

    /**
     * DOC qzhang Comment method "setPrefixToNamespace".
     * 
     * @param node
     */
    private void setPrefixToNamespace(Node node) {
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attr = nnm.item(i);
            String aname = attr.getNodeName();
            boolean isPrefix = aname.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":");
            if (isPrefix || aname.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                int index = aname.indexOf(':');
                String p = isPrefix ? aname.substring(index + 1) : XMLConstants.NULL_NS_URI;
                prefixToNamespace.put(p, attr.getNodeValue());
            }
        }
    }

    private String addDefaultNS(String xPathExpression) {
        if (XMLConstants.NULL_NS_URI.equals(getDefaultNamespace())) {
            return xPathExpression;
        } else {
            StringBuilder expr = new StringBuilder();
            String[] split = xPathExpression.split("/");
            for (String string : split) {
                if (!string.equals("") && string.indexOf(':') == -1 && string.indexOf('.') == -1) {
                    expr.append(DEFAULT_PRE + ":");
                }
                expr.append(string + "/");
            }
            if (split.length > 0) {
                expr.deleteCharAt(expr.length() - 1);
            }
            return expr.toString();
        }
    }

    /**
     * 
     * DOC amaumont Comment method "retrieveListOfNodes".
     * 
     * @param xPathExpression
     * @return always a <code>List</code> empty or not
     * @throws XPathExpressionException
     */
    public List<Node> retrieveListOfNodes(String xPathExpression) throws XPathExpressionException {

        NodeList nodeList = retrieveNodeList(xPathExpression);
        if (nodeList != null) {
            int nodeListLength = nodeList.getLength();
            List<Node> list = new ArrayList<Node>(nodeListLength);
            for (int j = 0; j < nodeListLength; ++j) {
                list.add(nodeList.item(j));
            }
            return list;
        } else {
            return new ArrayList<Node>(0);
        }

    }

    /**
     * DOC amaumont Comment method "retrieveNodeList".
     * 
     * @param xPathExpression
     * @param nodeList
     * @return <code>NodeList</code> or null if expression is invalid
     * @throws XPathExpressionException
     */
    public synchronized NodeList retrieveNodeList(String xPathExpression) throws XPathExpressionException {

        xPathExpression = simplifyXPathExpression(xPathExpression);
        xPathExpression = addDefaultNS(xPathExpression);
        NodeList nodeList = null;
        // System.out.println("xPathExpression = "+xPathExpression);
        XPathExpression xpathSchema = xpath.compile(xPathExpression);
        nodeList = (NodeList) xpathSchema.evaluate(document, XPathConstants.NODESET);
        return nodeList;
    }

    /**
     * DOC amaumont Comment method "simplifyXPathExpression".
     * 
     * @param pathExpression
     * @return
     */
    private static String simplifyXPathExpression(String xpathExpression) {

        Perl5Matcher matcher = new Perl5Matcher();

        Perl5Compiler compiler = new Perl5Compiler();

        Pattern pattern = null;
        try {
            pattern = compiler.compile("(.*)/\\s*\\w+\\s*(/(\\.\\.|parent))(.*)");
        } catch (MalformedPatternException e) {
            ExceptionHandler.process(e);
        }

        Perl5Substitution substitution = new Perl5Substitution("$1$4", Perl5Substitution.INTERPOLATE_ALL);

        int lengthOfPreviousXPath = 0;

        do {
            lengthOfPreviousXPath = xpathExpression.length();
            if (matcher.matches(xpathExpression, pattern)) {
                xpathExpression = Util.substitute(matcher, pattern, substitution, xpathExpression, Util.SUBSTITUTE_ALL);
            }
        } while (xpathExpression.length() != lengthOfPreviousXPath);

        return xpathExpression;
    }

    /**
     * DOC amaumont Comment method "retrieveNodeList".
     * 
     * @param xPathExpression
     * @param nodeList
     * @return <code>NodeList</code> or null if expression is invalid
     * @throws XPathExpressionException
     */
    public synchronized NodeList retrieveNodeListFromNode(String relativeXPathExpression, Node referenceNode)
            throws XPathExpressionException {
        relativeXPathExpression = simplifyXPathExpression(relativeXPathExpression);
        relativeXPathExpression = addDefaultNS(relativeXPathExpression);
        NodeList nodeList = (NodeList) xpath.evaluate(relativeXPathExpression, referenceNode, XPathConstants.NODESET);
        return nodeList;
    }

    /**
     * DOC amaumont Comment method "retrieveNodeList".
     * 
     * @param xPathExpression
     * @param nodeList
     * @return <code>NodeList</code> or null if expression is invalid
     * @throws XPathExpressionException
     */
    public synchronized Node retrieveNode(String xPathExpression) throws XPathExpressionException {
        xPathExpression = simplifyXPathExpression(xPathExpression);
        XPathExpression xpathSchema = xpath.compile(xPathExpression);
        Node node = (Node) xpathSchema.evaluate(document, XPathConstants.NODE);
        return node;
    }

    /**
     * DOC amaumont Comment method "retrieveNodeList".
     * 
     * @param relativeXPathExpression
     * @param nodeList
     * @return <code>NodeList</code> or null if expression is invalid
     * @throws XPathExpressionException
     */
    public synchronized Node retrieveNodeFromNode(String relativeXPathExpression, Node referenceNode)
            throws XPathExpressionException {
        relativeXPathExpression = simplifyXPathExpression(relativeXPathExpression);
        Node node = (Node) xpath.evaluate(relativeXPathExpression, referenceNode, XPathConstants.NODE);
        return node;
    }

    /**
     * DOC amaumont Comment method "retrieveNodeList".
     * 
     * @param xPathExpression
     * @param nodeList
     * @return <code>NodeList</code> or null if expression is invalid
     * @throws XPathExpressionException
     */
    public synchronized Double retrieveNodeCount(String xPathExpression) throws XPathExpressionException {
        XPathExpression xpathSchema = xpath.compile(xPathExpression);
        Object countNode = xpathSchema.evaluate(document, XPathConstants.NUMBER);
        return (Double) countNode;
    }

    public static void main(String[] args) throws XPathExpressionException {

        String string = simplifyXPathExpression("/doc/members/member/..");

        System.out.println(string);

        if (true) {
            return;
        }

        String filePath = "C:/test_xml/test.xml";

        XmlNodeRetriever pathRetriever = new XmlNodeRetriever(filePath, "");

        String currentExpr = "child::node()";
        // String currentExpr = "/doc/members";

        String xPathExpression;
        String slash = "/";
        if (currentExpr.endsWith("/")) {
            slash = "";
        }

        xPathExpression = currentExpr + slash + "";
        // xPathExpression = currentExpr + slash + "*" + " | " + currentExpr + slash + "@*";

        // 7689 nodes
        xPathExpression = "/doc/members/../members/member/child::*";
        xPathExpression = "/doc/members/../members/member/*";

        // 17925 nodes + text
        xPathExpression = "/doc/members/../members/member/child::node()";

        // 2547 attributes
        xPathExpression = "/doc/members/../members/member/@*";

        xPathExpression = "/doc/members/../members/member/*";
        xPathExpression = "/doc/members/member/../";

        String mainXPathNode = "/doc/members/member";
        String field1 = "..";
        String field2 = "summary/";

        System.out.println("main expression =" + mainXPathNode);
        Node mainNode = pathRetriever.retrieveNode(mainXPathNode);
        if (mainNode != null) {
            System.out.println("mainNode=" + mainNode.getNodeName());
        }

        Node field1Node = pathRetriever.retrieveNodeFromNode(field1, mainNode);
        if (field1Node != null) {
            System.out.println("field1Node=" + field1Node.getNodeName());
        }

        Node field2Node = pathRetriever.retrieveNodeFromNode(field2, mainNode);
        if (field2Node != null) {
            System.out.println("field2Node=" + field2Node.getNodeName());
        }

        String proposal1 = "../*";
        String proposal2 = "./member/see/*";
        proposal2 = "member/* | member/@*";
        NodeList proposal1Nodes = pathRetriever.retrieveNodeListFromNode(proposal1, mainNode);
        if (field1Node != null) {
            int length = proposal1Nodes.getLength();
            System.out.println("proposal1Nodes : " + length);
            // int lstSize = length;
            // for (int i = 0; i < lstSize; i++) {
            // System.out.println(proposal1Nodes.item(i));
            // }
        }

        NodeList proposal2Nodes = pathRetriever.retrieveNodeListFromNode(proposal2, mainNode);
        if (proposal2Nodes != null) {
            int length = proposal2Nodes.getLength();
            System.out.println("proposal2Nodes : " + length);
            // int lstSize = length;
            // for (int i = 0; i < lstSize; i++) {
            // System.out.println(proposal2Nodes.item(i));
            // }
        }

        // if (true) {
        // return;
        // }

        // String countXPathExpression = null;
        // countXPathExpression = xPathExpression + "[count(*)]";
        // countXPathExpression = "count(" + xPathExpression + ")";
        // countXPathExpression = xPathExpression + "self::count()";
        // System.out.println("countXPathExpression = " + countXPathExpression);
        // TimeMeasure.start("count");
        // Double count = pathRetriever.retrieveNodeCount(countXPathExpression);
        // TimeMeasure.end("count");
        //
        // System.out.println("count=" + count);
        //
        // TimeMeasure.start("nodeList");
        // NodeList nodeList2 = pathRetriever.retrieveNodeList(xPathExpression);
        // System.out.println("Count result : " + nodeList2.getLength());
        // TimeMeasure.end("nodeList");
        //
        if (true) {
            return;
        }

        System.out.println("xPathExpression = '" + xPathExpression + "'");
        List<Node> nodeList = pathRetriever.retrieveListOfNodes(xPathExpression);

        System.out.println("Count result : " + nodeList.size());

        int lstSize = nodeList.size();
        int limit = 100;
        for (int i = 0; i < lstSize; i++) {
            if (i > limit) {
                break;
            }
            Node node = nodeList.get(i);
            System.out.println(node.getNodeName());

        }

        // for (Node node : nodeList) {
        // // System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
        // System.out.println(node.getNodeName());
        // }

    }

    /**
     * DOC amaumont Comment method "getAbsoluteXPathFromNode".
     * 
     * @param node
     */
    public static String getAbsoluteXPathFromNode(Node node) {
        return getAbsoluteXPathFromNode(node, "");
    }

    /**
     * DOC amaumont Comment method "getAbsoluteXPathFromNode".
     * 
     * 
     * @param node
     * @param string
     */
    private static String getAbsoluteXPathFromNode(Node node, String currentXPath) {
        Node parentNode = null;
        String at = "";
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            parentNode = ((Attr) node).getOwnerElement();
            at = STRING_AT;
        } else {
            parentNode = node.getParentNode();
            at = STRING_EMPTY;
        }
        currentXPath = "/" + at + node.getNodeName() + currentXPath;
        if (parentNode == node.getOwnerDocument()) {
            return currentXPath;
        } else {
            return getAbsoluteXPathFromNode(parentNode, currentXPath);
        }
    }

    /**
     * Getter for document.
     * 
     * @return the document
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * DOC amaumont Comment method "setCurrentLoopXPath".
     * 
     * @param newValue
     */
    public void setCurrentLoopXPath(String currentLoopXPath) {
        this.currentLoopXPath = currentLoopXPath;
    }

    /**
     * Getter for currentLoopXPath.
     * 
     * @return the currentLoopXPath
     */
    public String getCurrentLoopXPath() {
        return this.currentLoopXPath;
    }

    /**
     * DOC amaumont Comment method "dispose".
     */
    public void dispose() {
        if (document != null) {
            // document.re
        }
    }

}
