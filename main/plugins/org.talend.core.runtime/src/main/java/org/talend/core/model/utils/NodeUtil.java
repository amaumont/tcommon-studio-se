// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.core.model.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.talend.core.model.components.IComponent;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.AbstractNode;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;

/**
 * DOC xtan class global comment. Detailled comment <br/>
 * 
 */
public class NodeUtil {

    /**
     * DOC sort the outgoingconnections to make sure the first connection is EConnectionType.FLOW_MAIN or
     * EConnectionType.FLOW_REF<br/>
     * <p>
     * bug:9363, if a component have 2 output links,
     * <li>"EConnectionType.FLOW_MAIN(FLOW), EConnectionType.FLOW_REF(REJECT)"</li>
     * <li>"EConnectionType.FLOW_MAIN(REJECT), EConnectionType.FLOW_REF(FLOW)"</li>, make FLOW before "REJECT"
     * </p>
     * 
     * @param node
     * @return List<? extends IConnection>
     */

    public static List<? extends IConnection> getOutgoingCamelSortedConnections(INode node) {
        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>(outgoingConnections);
            Collections.sort(conns, new Comparator<IConnection>() {

                private int getTypeWeighted(IConnection con) {
                    switch (con.getLineStyle()) {
                    case ROUTE_ENDBLOCK:
                        return 100;
                    case ROUTE:
                        return 90;
                    case ROUTE_OTHER:
                        return 80;
                    case ROUTE_WHEN:
                        return 70;
                    case ROUTE_FINALLY:
                        return 50;
                    case ROUTE_CATCH:
                        return 40;
                    case ROUTE_TRY:
                        return 30;
                    default:
                        return 0;
                    }
                }

                @Override
                public int compare(IConnection o1, IConnection o2) {
                    int weightedGap = getTypeWeighted(o1) - getTypeWeighted(o2);
                    if (weightedGap == 0) {
                        // same style, compare by inputId
                        if (o1.getOutputId() > o2.getOutputId()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                    return weightedGap;
                }
            });
        }

        return conns;
    }

    public static List<? extends IConnection> getOutgoingSortedConnections(INode node) {

        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>(outgoingConnections);
            Collections.sort(conns, new Comparator<IConnection>() {

                @Override
                public int compare(IConnection connection1, IConnection connection2) {

                    EConnectionType lineStyle = connection1.getLineStyle();
                    EConnectionType lineStyle2 = connection2.getLineStyle();
                    // "FLOW" only for the default Main connection, if user define his connection like: "FILTER",
                    // "REJECT", "FLOW", he should use this API in JET directly: List<? extends IConnection> connsFilter
                    // = node.getOutgoingConnections("FILTER");
                    // 1. FLOW first
                    if ("FLOW".equals(connection1.getConnectorName())) { //$NON-NLS-1$
                        return -1;
                    }

                    if ("FLOW".equals(connection2.getConnectorName())) { //$NON-NLS-1$
                        return 1;
                    }

                    // 2. FLOW_MAIN, FLOW_MERGE second
                    if (lineStyle == EConnectionType.FLOW_MAIN || lineStyle == EConnectionType.FLOW_MERGE) {
                        return -1;
                    }
                    if (lineStyle2 == EConnectionType.FLOW_MAIN || lineStyle2 == EConnectionType.FLOW_MERGE) {
                        return 1;
                    }

                    // 3. others cases, the last
                    return 0;

                }
            });
        }

        return conns;
    }

    /**
     * DOC get the EConnectionType.FLOW_MAIN or EConnectionType.FLOW_REF out goning Connections<br/>
     * 
     * @param node
     * @return List<? extends IConnection>
     */
    public static List<? extends IConnection> getMainOutgoingConnections(INode node) {
        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < outgoingConnections.size(); i++) {

                IConnection connection = outgoingConnections.get(i);
                if (connection.getLineStyle() == EConnectionType.FLOW_MAIN
                        || connection.getLineStyle() == EConnectionType.FLOW_REF) {

                    conns.add(connection);

                }
            }
        }
        return conns;
    }

    public static List<? extends IConnection> getOutgoingConnections(INode node, EConnectionType connectionType) {
        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < outgoingConnections.size(); i++) {
                IConnection connection = outgoingConnections.get(i);
                if (connection.getLineStyle() == connectionType) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    /**
     * DOC get the EConnectionType.FLOW_MAIN in coming Connections<br/>
     * 
     * @param node
     * @return INode
     */
    public static INode getMainIncomingConnections(INode node) {
        List<? extends IConnection> incomingConnections = node.getIncomingConnections();
        if (incomingConnections != null) {
            for (int i = 0; i < incomingConnections.size(); i++) {
                IConnection connection = incomingConnections.get(i);
                if (connection.getLineStyle() == EConnectionType.FLOW_MAIN) {
                    return connection.getSource();
                }
            }
        }
        return null;
    }

    public static List<? extends IConnection> getIncomingConnections(INode node, EConnectionType connectionType) {
        List<IConnection> conns = null;

        List<? extends IConnection> incomingConnections = node.getIncomingConnections();
        if (incomingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < incomingConnections.size(); i++) {
                IConnection connection = incomingConnections.get(i);
                if (connection.getLineStyle() == connectionType) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    public static List<? extends IConnection> getOutgoingConnections(INode node, String connectorName) {
        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < outgoingConnections.size(); i++) {

                IConnection connection = outgoingConnections.get(i);
                if (connectorName.equals(connection.getConnectorName())) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    /**
     * 
     * wzhang Comment method "getIncomingConnections".
     * 
     * @param node
     * @param category
     * @return
     */
    public static List<? extends IConnection> getIncomingConnections(INode node, int category) {
        List<IConnection> conns = null;

        List<? extends IConnection> incomingConnections = node.getIncomingConnections();
        if (incomingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < incomingConnections.size(); i++) {

                IConnection connection = incomingConnections.get(i);
                if (connection.getLineStyle().hasConnectionCategory(category)) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    public static List<? extends IConnection> getOutgoingConnections(INode node, int category) {
        List<IConnection> conns = null;

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < outgoingConnections.size(); i++) {

                IConnection connection = outgoingConnections.get(i);
                if (connection.getLineStyle().hasConnectionCategory(category)) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    public static List<IMetadataTable> getIncomingMetadataTable(INode node, int category) {
        List<IMetadataTable> metadatas = new ArrayList<IMetadataTable>();

        List<? extends IConnection> incomingConnections = node.getIncomingConnections();
        if (incomingConnections != null) {
            for (int i = 0; i < incomingConnections.size(); i++) {

                IConnection connection = incomingConnections.get(i);
                if (connection.getLineStyle().hasConnectionCategory(category)) {
                    metadatas.add(connection.getMetadataTable());
                }
            }
        }
        return metadatas;
    }

    public static List<? extends IConnection> getIncomingConnections(INode node, String connectorName) {
        List<IConnection> conns = null;

        List<? extends IConnection> incomingConnections = node.getIncomingConnections();
        if (incomingConnections != null) {
            conns = new ArrayList<IConnection>();

            for (int i = 0; i < incomingConnections.size(); i++) {

                IConnection connection = incomingConnections.get(i);
                if (connectorName.equals(connection.getConnectorName())) {
                    conns.add(connection);
                }
            }
        }
        return conns;
    }

    public static boolean checkConnectionAfterNode(INode node, EConnectionType connectionType, List<INode> checkedNodes) {
        // fix bug 0004935: Error on job save
        if (checkedNodes.contains(node)) {
            return false;
        } else {
            checkedNodes.add(node);
        }

        boolean result = false;
        List<? extends IConnection> outConns = getOutgoingConnections(node, connectionType);
        if (outConns == null || outConns.size() == 0) {
            List<? extends IConnection> conns = getOutgoingSortedConnections(node);
            if (conns != null && conns.size() > 0) {
                for (IConnection conn : conns) {
                    result = checkConnectionAfterNode(conn.getTarget(), connectionType, checkedNodes);
                    if (result) {
                        break;
                    }
                }
            } else {
                result = false;
            }
        } else {
            result = true;
        }
        return result;
    }

    public static boolean checkComponentErrorConnectionAfterNode(INode node) {
        List<INode> checkedNodes = new ArrayList<INode>();
        return checkConnectionAfterNode(node, EConnectionType.ON_COMPONENT_ERROR, checkedNodes);
    }

    /**
     * DOC xtan
     * <p>
     * InLineJob means all nodes after a iterate link(The nodes will execute many times on every iterate).
     * </p>
     * Notice: The search method don't consider the second branch of the tUnite, but it is ok.
     * 
     * @param node
     * @return
     */
    public static Set<? extends IConnection> getAllInLineJobConnections(INode node) {
        Set<String> uniqueNamesDone = new HashSet<String>();
        uniqueNamesDone.add(node.getUniqueName());
        return getAllInLineJobConnections(node, uniqueNamesDone);
    }

    private static Set<? extends IConnection> getAllInLineJobConnections(INode node, Set<String> uniqueNamesDone) {
        Set<IConnection> conns = new HashSet<IConnection>();

        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null) {

            conns.addAll(outgoingConnections); // add all

            for (int i = 0; i < outgoingConnections.size(); i++) {

                IConnection connection = outgoingConnections.get(i);
                INode nextNode = connection.getTarget();

                if (!uniqueNamesDone.contains(nextNode.getUniqueName())) {
                    uniqueNamesDone.add(nextNode.getUniqueName());
                    conns.addAll(getAllInLineJobConnections(nextNode, uniqueNamesDone)); // follow this way
                }
            }
        }
        return conns;
    }

    public static INode getFirstMergeNode(INode node) {
        INode mergeNode = null;
        for (IConnection connection : node.getOutgoingConnections()) {
            if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.MERGE)) {
                mergeNode = connection.getTarget();
            } else if (connection.getLineStyle().hasConnectionCategory(IConnectionCategory.FLOW)) {
                mergeNode = getFirstMergeNode(connection.getTarget());
            }
            if (mergeNode != null) {
                break;
            }
        }
        return mergeNode;
    }

    public static IConnection getNextMergeConnection(INode node) {

        List<? extends IConnection> outConns = getOutgoingConnections(node, IConnectionCategory.MERGE);
        if (outConns == null || outConns.size() == 0) {
            List<? extends IConnection> conns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);
            if (conns != null && conns.size() > 0) {
                for (IConnection conn : conns) {
                    node = conn.getTarget();
                    if (node.isActivate() || node.isDummy()) {
                        IConnection connection = getNextMergeConnection(node);
                        if (connection != null) {
                            return connection;
                        }
                    }
                }
            } else if (node.getVirtualLinkTo() != null) {
                return getNextMergeConnection(node.getOutgoingConnections().get(0).getTarget());
            }
            return null;
        } else {
            return outConns.get(0);
        }
    }

    /**
     * DOC bchen
     * <p>
     * This method works for the virtual component which the inner link is ON_COMPONENT_OK and the output connection
     * contains Merge type and the order is not 1.
     * </p>
     * <p>
     * notice: 1. the node is not in main branch, so there is not possible to have ON_SUBJOB_OK/ON_COMPONENT_OK in
     * start.
     * 
     * 2. if there are two or more tUnite components, it will be tHash virtual component, no inner link in this virtual
     * component
     * 
     * 3. if there have tIteratToFlow component, no inner link ,as this component also adapt to Merge connection.
     * </p>
     * <p>
     * return: 1. if there have another virtual component(ON_COMPONENT_OK) in the incoming connection, it will be
     * returned.
     * 
     * 2. if there have not another virtual component(ON_COMPENT_OK), it will return the start node as it ignore other
     * type of virtual components
     * </p>
     * 
     * @param node
     * @return node
     */
    public static INode getSpecificStartNode(INode node) {
        List<? extends IConnection> inConns = node.getIncomingConnections();
        if (inConns == null || inConns.size() == 0) {
            return node;
        } else {
            if (inConns.size() == 1 && inConns.get(0).getLineStyle().equals(EConnectionType.ON_COMPONENT_OK)) {
                INode sNode = inConns.get(0).getSource();
                if (sNode != null && sNode.getVirtualLinkTo() != null
                        && sNode.getVirtualLinkTo().equals(EConnectionType.ON_COMPONENT_OK)) {
                    return node;
                }
            }
            for (IConnection inConn : inConns) {
                INode sourceNode = inConn.getSource();
                if (inConn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || sourceNode.getVirtualLinkTo() != null) {
                    INode activeNode = findActiveNode(sourceNode);
                    if (activeNode != null) {
                        INode findNode = getSpecificStartNode(activeNode);
                        if (findNode != null) {
                            return findNode;
                        }
                    }
                }

            }
        }
        return null;
    }

    private static INode findActiveNode(INode node) {
        if (node.isActivate()) {
            return node;
        } else if (node.isDummy()) {
            if (node.getIncomingConnections() != null && node.getIncomingConnections().size() == 1) {
                return findActiveNode(node.getIncomingConnections().get(0).getSource());
            }
        }
        return null;
    }

    public static boolean isLastFromMergeTree(INode node) {
        INode firstMergeNode = getFirstMergeNode(node);
        int totMerge = NodeUtil.getIncomingConnections(firstMergeNode, IConnectionCategory.MERGE).size();
        Integer posMerge = node.getLinkedMergeInfo().get(firstMergeNode);
        return totMerge == posMerge;
    }

    public static IConnection getRealConnectionTypeBased(IConnection connection) {
        IConnection realConnection = connection;

        boolean connectionAvailable = true;

        //        while (connectionAvailable && realConnection.getSource().getComponent().getName().equals("tReplace")) { //$NON-NLS-1$

        while (connectionAvailable && !realConnection.getSource().isSubProcessStart()
                && realConnection.getSource().getComponent().isDataAutoPropagated()) {

            List<IConnection> inConnections = (List<IConnection>) getIncomingConnections(realConnection.getSource(),
                    IConnectionCategory.FLOW);
            if (inConnections.size() > 0) {
                realConnection = inConnections.get(0);
            } else {
                connectionAvailable = false;
            }
        }

        return realConnection;
    }

    /**
     * DOC wliu
     * <p>
     * judge if the current connection is the last output connection of the component
     * </p>
     * Notice: It is used in subtree_end.javajet. And the aim is for feature5996
     * 
     * @param connection
     * @return
     */
    public static boolean isLastMultiplyingOutputComponents(IConnection connection) {

        List<? extends IConnection> conns = connection.getSource().getOutgoingConnections();
        int last = -1;
        if (conns != null && conns.size() > 0) {
            for (int i = 0; i < conns.size(); i++) {
                if (conns.get(i).getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                    last = i;
                }
            }
        }

        if (last >= 0 && connection.getName().equals(conns.get(last).getName())) {
            return true;
        }

        return false;
    }

    /**
     * 
     * judge if the current node is in the last branch Notice: It is only used in tPigStoreResult. And the aim is for
     * TDI-25120
     * 
     * @param node
     * @return
     */
    public static boolean isSubTreeEnd(INode node) {
        List<? extends IConnection> incConnections = NodeUtil.getIncomingConnections(node, IConnectionCategory.DATA);
        if (incConnections.size() > 0) {
            IConnection connection = incConnections.get(0); // always take the first one, don't consider merge case.
            if (isLastMultiplyingOutputComponents(connection)) {
                return isSubTreeEnd(connection.getSource());
            } else {
                return false;
            }
        }
        return true;
    }

    public static Map<INode, Integer> getLinkedMergeInfo(final INode node) {
        Map<INode, Integer> map = new HashMap<INode, Integer>();

        getLinkedMergeInfo(node, map);

        if (map.isEmpty()) {
            // in case the component is not linked directly, it should take the status of previous component, since it
            // will be in the same branch.
            getLinkedMergeInfoFromMainLink(node, map);
        }

        return map;
    }

    private static void getLinkedMergeInfoFromMainLink(final INode node, final Map<INode, Integer> map) {
        if (node.getComponent().useMerge()) {
            return;
        }
        List<IConnection> inputConnections = (List<IConnection>) getIncomingConnections(node, IConnectionCategory.MAIN);
        if (inputConnections.size() > 0) {
            IConnection input = inputConnections.get(0);
            INode sourceNode = input.getSource();
            if (sourceNode.getJobletNode() != null) {
                sourceNode = sourceNode.getJobletNode();
            }
            getLinkedMergeInfo(sourceNode, map);
            if (map.isEmpty()) {
                getLinkedMergeInfoFromMainLink(sourceNode, map);
            }
        }

    }

    private static void getLinkedMergeInfo(final INode node, final Map<INode, Integer> map) {
        List<? extends IConnection> outgoingConnections = node.getOutgoingConnections();
        for (int i = 0; (i < outgoingConnections.size()); i++) {
            IConnection connec = outgoingConnections.get(i);
            if (connec.isActivate()) {
                if (connec.getLineStyle().hasConnectionCategory(EConnectionType.MERGE)) {
                    INode jNode = connec.getTarget();
                    if (jNode.getJobletNode() != null) {
                        jNode = jNode.getJobletNode();
                    }
                    map.put(jNode, connec.getInputId());
                    getLinkedMergeInfo(jNode, map);
                } else if (connec.getLineStyle().hasConnectionCategory(EConnectionType.MAIN) && connec.getTarget() != null) {
                    INode jNode = connec.getTarget();
                    if (jNode.getJobletNode() != null) {
                        jNode = jNode.getJobletNode();
                    }
                    getLinkedMergeInfo(jNode, map);
                }
            }
        }
    }

    // this is only for bug:11754, and only be used in generating code.
    public static boolean isDataAutoPropagated(final INode node) {
        IComponent component = node.getComponent();
        // if it is tJavaFlex, use the property Version_V2_0 to instead the DATA_AUTO_PROPAGATE="false" config
        // in tJavaFlex_java.xml
        if (component.getName().compareTo("tJavaFlex") == 0) {
            boolean isVersionV20 = "true".equals(ElementParameterParser.getValue(node, "__Version_V2_0__"));
            return isVersionV20;
        } else {
            return component.isDataAutoPropagated();
        }
    }

    /**
     * DOC wliu
     * <p>
     * get the original connection instance className of the pamameter:conn.\n It is used to help optimize the code to
     * avoid 65535 bytes in a method
     * </p>
     * Notice: It is used in tFileOutputMSXML in TDI-21606
     * 
     * @param connection
     * @return
     */
    public static String getPrivateConnClassName(final IConnection conn) {

        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            INode node = conn.getSource();
            if (node.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(node))) {
                return conn.getName();
            }
            List<? extends IConnection> listInConns = node.getIncomingConnections();
            if (listInConns != null && listInConns.size() > 0) {
                String retResult = getPrivateConnClassName(listInConns.get(0));
                if (retResult == null) {
                    return conn.getName();
                } else {
                    return retResult;
                }
            }
        }
        return null;

    }

    /**
     * DOC jyhu
     * <p>
     * function:get the node from generating nodes by unique name. aim:to get the property value from any node.
     * </p>
     * Notice: It is used to get property values from the pointed node.
     * 
     * @param node:node from the job @param uniqueName:the unique name of the pointed node.
     * @return
     */
    public static INode getNodeByUniqueName(final IProcess process, String uniqueName) {

        List<INode> nodes = (List<INode>) process.getGeneratingNodes();
        INode return_node = null;
        for (INode current_node : nodes) {
            if (uniqueName.equals(current_node.getUniqueName())) {
                return_node = current_node;
            }
        }
        return return_node;
    }

    /**
     * 
     * DOC liuwu find all the tRecollectors which match to tPartitioner
     * 
     * @param node : should be tPartitioner
     * @param recollectors
     */
    public static void getRecollectorsFromPartitioner(INode node, List<String> recollectors) {
        List<? extends INode> listRecollectors = node.getProcess().getNodesOfType("tRecollector"); //$NON-NLS-1$
        if (listRecollectors != null && listRecollectors.size() > 0) {
            for (INode recNode : listRecollectors) {
                String departitionerName = ElementParameterParser.getValue(recNode, "__DEPART_COMPONENT__"); //$NON-NLS-1$
                List<? extends INode> listDepartitioner = node.getProcess().getNodesOfType("tDepartitioner"); //$NON-NLS-1$
                if (listDepartitioner == null) {
                    return;
                }

                for (INode tnode : listDepartitioner) {
                    if (tnode.getUniqueName().equals(departitionerName)) { // find the tDepartitioner corresponding to
                                                                           // tRecollector
                        INode startNode = getSubProcessStartNode(tnode); // find the tCollector
                        List<? extends IConnection> inConns = startNode.getIncomingConnections(EConnectionType.STARTS);
                        if (inConns != null && inConns.size() > 0) {
                            if (inConns.get(0).getSource() == node) {
                                recollectors.add(recNode.getUniqueName());
                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    /**
     * @author jyhu
     * @aim Get Whether the nodelist contain virtual component.
     * @param nodeList: Node list
     * @return nodelist contain virtual component or not. true:contain;false:not contain
     */
    public static boolean hasVirtualComponent(List<? extends INode> nodeList) {
        boolean hasVirtualComponent = false;
        for (INode node : nodeList) {
            if (node.isVirtualGenerateNode()) {
                hasVirtualComponent = true;
                break;
            }
        }
        return hasVirtualComponent;
    }

    /**
     * @author jyhu
     * @aim Get unique name of the graphica node from generating node.
     * @param node: Generated node
     * @return unique name of the graphica node.
     */
    public static String getVirtualUniqueName(INode node) {
        return getVirtualNode(node).getUniqueName();
    }

    /**
     * @author jyhu
     * @aim Get graphica node from generating node.
     * @param node: Generated node
     * @return graphica node.
     */
    public static INode getVirtualNode(INode node) {
        INode virtualNode = node;
        if (node.isVirtualGenerateNode()) {
            String uniqueName = node.getUniqueName();
            List<? extends INode> nodeList = node.getProcess().getGraphicalNodes();
            for (INode graphicnode : nodeList) {
                if (graphicnode.isGeneratedAsVirtualComponent()) {
                    String nodeUniqueName = graphicnode.getUniqueName();
                    if (uniqueName.indexOf(nodeUniqueName + "_") == 0) {
                        virtualNode = graphicnode;
                        break;
                    }
                }
            }
        }
        return virtualNode;
    }

    public static void fillConnectionsForStat(List<String> connsName, INode currentNode) {
        for (IConnection conn : currentNode.getOutgoingConnections()) {
            if (conn.getLineStyle() == EConnectionType.FLOW_MAIN) {
                if (!(currentNode.isVirtualGenerateNode() && currentNode.getVirtualLinkTo() != null)) {
                    // if the conn between two virtual compnents, don't consider
                    connsName.add(conn.getUniqueName());
                }
                fillConnectionsForStat(connsName, conn.getTarget());
            } else if (conn.getLineStyle() == EConnectionType.FLOW_MERGE) {
                connsName.add(conn.getUniqueName());
                continue;
            } else if (conn.getLineStyle() == EConnectionType.ON_ROWS_END) {
                // on_rows_end only used for virtual component, so don't need to consider
                fillConnectionsForStat(connsName, conn.getTarget());
            }
        }
    }

    /*
     * return all displayed parameters
     */
    public static List<IElementParameter> getDisplayedParameters(INode currentNode) {
        List<? extends IElementParameter> eps = currentNode.getElementParameters();
        List<IElementParameter> reps = new ArrayList<IElementParameter>();
        // should ignore Parallelize?
        List<String> ignorePs = Arrays.asList("CONNECTION_FORMAT", "INFORMATION", "COMMENT", "VALIDATION_RULES", "LABEL", "HINT",
                "ACTIVATE", "TSTATCATCHER_STATS", "PARALLELIZE", "PROPERTY", "SQLPATTERN_VALUE");
        // Exclude SQLPATTERN_VALUE.
        for (IElementParameter ep : eps) {
            if (ep.isShow(eps)) {
                if (!ignorePs.contains(ep.getName())) {
                    reps.add(ep);
                }
            }
        }
        return reps;
    }

    public static String getNormalizeParameterValue(INode node, String elementName) {
        List<? extends IElementParameter> eps = node.getElementParameters();
        for (IElementParameter ep : eps) {
            if (elementName.equals(ep.getName())) {
                return getNormalizeParameterValue(node, ep);
            }
        }
        throw new IllegalArgumentException();
    }

    public static String getNormalizeParameterValue(INode node, IElementParameter ep) {
        if (EParameterFieldType.TABLE.equals(ep.getFieldType())) {
            Map<String, IElementParameter> types = new HashMap<String, IElementParameter>();
            for (Object o : ep.getListItemsValue()) {
                IElementParameter cep = (IElementParameter) o;
                if (cep.isShow(node.getElementParameters())) {
                    types.put(cep.getName(), cep);
                }
            }
            List<Map<String, String>> lines = (List<Map<String, String>>) ElementParameterParser.getObjectValue(node,
                    "__" + ep.getName() + "__");
            StringBuilder value = new StringBuilder();
            // implement List & Map toString(), different is the value of Map
            Iterator<Map<String, String>> linesIter = lines.iterator();
            if (!linesIter.hasNext()) {
                return "\"[]\"";
            }
            value.append("\"[");
            for (;;) {
                Map<String, String> columns = linesIter.next();
                Iterator<Entry<String, String>> columnsIter = columns.entrySet().iterator();

                value.append("{");
                Entry<String, String> column = null;
                boolean printedColumnExist = false;
                while (columnsIter.hasNext()) {
                    column = columnsIter.next();
                    if (types.get(column.getKey()) == null) {
                        continue;
                    }
                    printedColumnExist = true;

                    value.append(column.getKey());
                    value.append("=\"+(");

                    value.append(getNormalizeParameterValue(column.getValue(), types.get(column.getKey()), true));

                    value.append(")+\"");

                    if (columnsIter.hasNext()) {
                        value.append(", ");
                    }
                }
                if (printedColumnExist && column != null && (types.get(column.getKey()) == null)) {
                    value.setLength(value.length() - 2);
                }
                value.append("}");

                if (!linesIter.hasNext()) {
                    return value.append("]\"").toString();
                }
                value.append(",").append(" ");
            }
        } else {
            String value = ElementParameterParser.getValue(node, "__" + ep.getName() + "__");
            if (EParameterFieldType.TABLE_BY_ROW.equals(ep.getFieldType())) {
                value = ep.getValue().toString();
            }
            return getNormalizeParameterValue(value, ep, false);
        }

    }

    private static String getNormalizeParameterValue(String value, IElementParameter ep, boolean itemFromTable) {
        // !!!no need these code because we treat it when ep.isRaw()
        // List<EParameterFieldType> escapeQuotation = Arrays.asList(EParameterFieldType.MEMO_JAVA,
        // EParameterFieldType.SCHEMA_XPATH_QUERYS);// TODO: no need for SCHEMA_XPATH_QUERYS, and remove the
        // // RAW="true" on component xml
        // if (escapeQuotation.contains(ep.getFieldType()) && ep.isRaw()) {
        // value = value.replaceAll("\\\\", "\\\\\\\\");
        // value = value.replaceAll("\\\"", "\\\\\\\"");
        // }
        List<EParameterFieldType> needRemoveCRLFList = Arrays.asList(EParameterFieldType.MEMO, EParameterFieldType.MEMO_JAVA,
                EParameterFieldType.MEMO_SQL, EParameterFieldType.MEMO_IMPORT, EParameterFieldType.MEMO_MESSAGE);
        if (needRemoveCRLFList.contains(ep.getFieldType())) {
            value = value.replaceAll("[\r\n]", " ");
        }
        List<EParameterFieldType> needQuoteList = Arrays.asList(EParameterFieldType.CLOSED_LIST,
                EParameterFieldType.COMPONENT_LIST, EParameterFieldType.COLUMN_LIST, EParameterFieldType.PREV_COLUMN_LIST,
                EParameterFieldType.CONNECTION_LIST, EParameterFieldType.LOOKUP_COLUMN_LIST,
                EParameterFieldType.CONTEXT_PARAM_NAME_LIST, EParameterFieldType.PROCESS_TYPE, EParameterFieldType.COLOR,
                EParameterFieldType.TABLE_BY_ROW, EParameterFieldType.HADOOP_JARS_DIALOG);
        List<EParameterFieldType> needQuoteListForItem = itemFromTable ? Arrays.asList(EParameterFieldType.SCHEMA_TYPE,
                EParameterFieldType.SAP_SCHEMA_TYPE, EParameterFieldType.MODULE_LIST) : new ArrayList<EParameterFieldType>();
        // TODO: add RAW attribute when SCHEMA_COLUMN generated by BASED_ON_SCHEMA
        List<String> needQuoteListByName = Arrays.asList("SCHEMA_COLUMN");// SCHEMA_COLUMN for BASED_ON_SCHEMA="true"

        if (needQuoteList.contains(ep.getFieldType()) || needQuoteListForItem.contains(ep.getFieldType())
                || needQuoteListByName.contains(ep.getName()) || ep.isRaw()) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
            value = value.replaceAll("\\\"", "\\\\\\\"");
            value = "\"" + value + "\"";
        }

        if (itemFromTable) {
            if ("*".equals(value)) {
                value = "\"" + value + "\"";
            }
        }

        // copied it from Log4jFileUtil.javajet but need more comment for this script
        if (value == null || "".equals(value.trim())) {// for the value which empty
            value = "\"\"";
        } else if ("\"\\n\"".equals(value) || "\"\\r\"".equals(value) || "\"\\r\\n\"".equals(value)) {
            // for the value is "\n" "\r" "\r\n"
            value = value.replaceAll("\\\\", "\\\\\\\\");
        } else if ("\"\"\"".equals(value)) {
            value = "\"" + "\\" + "\"" + "\"";
        } else if ("\"\"\\r\\n\"\"".equals(value)) {
            value = "\"\\\\r\\\\n\"";
        } else if ("\"\"\\r\"\"".equals(value)) {
            value = "\"\\\\r\"";
        } else if ("\"\"\\n\"\"".equals(value)) {
            value = "\"\\\\n\"";
        }
        // ftom 20141008 - patch to fix javajet compilation errors due to hard-coded studio TableEditor mechanism
        // linked to BUILDIN properties checks, this item is a boolean set to TRUE or FALSE
        // fix is just transforming into true or false to make logging OK
        else if ("BUILDIN".equals(ep.getName())) {
            value = value.toLowerCase();
        }
        return value;
    }

    /**
     * 
     * DOC liuwu Comment method "replaceMEMO_SQL". aim: to resolve TDI-7487
     * 
     * @param original
     * @return
     */
    public static String replaceCRLFInMEMO_SQL(String original) {
        if (original == null || original.trim().length() == 0) {
            return original;
        }
        String result = "";
        int leftQuotes = original.indexOf("\"");
        int rightQuotes = original.indexOf("\"", leftQuotes + 1);
        int fakeRightQuotes = original.indexOf("\\\"", leftQuotes + 1);
        while (rightQuotes == fakeRightQuotes + 1) {
            rightQuotes = original.indexOf("\"", rightQuotes + 1);
            fakeRightQuotes = original.indexOf("\\\"", fakeRightQuotes + 1);
        }
        int leftPrev = 0;
        while (leftQuotes >= 0 && rightQuotes > leftQuotes) {
            if (leftQuotes > leftPrev) {
                result += original.substring(leftPrev, leftQuotes);
            }
            // System.out.println("leftQuote="+leftQuotes + ", rightQuote="+rightQuotes);
            if (leftQuotes < rightQuotes) {
                result += original.substring(leftQuotes, rightQuotes + 1).replace("\r", "").replace("\n", "\\n");
            }

            leftQuotes = original.indexOf("\"", rightQuotes + 1);
            leftPrev = rightQuotes + 1;
            rightQuotes = original.indexOf("\"", leftQuotes + 1);
            fakeRightQuotes = original.indexOf("\\\"", leftQuotes + 1);
            while (rightQuotes == fakeRightQuotes + 1) {
                rightQuotes = original.indexOf("\"", rightQuotes + 1);
                fakeRightQuotes = original.indexOf("\\\"", fakeRightQuotes + 1);
            }
        }
        result += original.substring(leftPrev);
        return result;
    }

    /**
     * 
     * add it for TDI-28503
     * 
     * @param departitioner node in collector subprocess
     * @return collector node as the start node
     */
    public static INode getSubProcessStartNode(INode currentNode) {
        int nb = 0;
        for (IConnection connection : currentNode.getIncomingConnections()) {
            if (connection.isActivate()) {
                nb++;
            }
        }
        if (nb == 0) {
            return currentNode;
        }
        IConnection connec;

        for (int j = 0; j < currentNode.getIncomingConnections().size(); j++) {
            connec = currentNode.getIncomingConnections().get(j);
            if (((AbstractNode) connec.getSource()).isOnMainMergeBranch()) {
                if (connec.getLineStyle() == EConnectionType.STARTS) {
                    return currentNode;
                } else if (connec.getLineStyle() != EConnectionType.FLOW_REF) {
                    return getSubProcessStartNode(connec.getSource());
                }
            }
        }
        return null;
    }
}
