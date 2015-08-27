package hpoweb.uicontent.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.graph.Arc.Direction;
import com.vaadin.graph.GraphRepository;

/**
 * Simple memory-only implementation of GraphRepository interface
 *
 */
public class SimpleGraphRepositoryImpl implements GraphRepository<NodeImpl, ArcImpl>, Serializable {

	private static final long serialVersionUID = 1L;

	private String homeNodeId;

	private Map<String, NodeImpl> nodeMap = new HashMap<String, NodeImpl>();

	private Map<String, ArcImpl> edgeMap = new HashMap<String, ArcImpl>();

	// edge-id -> head-node-id
	private Map<String, String> headMap = new HashMap<String, String>();

	// edge-id -> tail-node-id
	private Map<String, String> tailMap = new HashMap<String, String>();

	// node-id -> incoming-edge-id
	private Map<String, Set<String>> incomingMap = new HashMap<String, Set<String>>();

	// node-id -> outgoing-edge-id
	private Map<String, Set<String>> outgoingMap = new HashMap<String, Set<String>>();

	// private Map<String, GNode> nodeMap;

	public NodeImpl getTail(ArcImpl arc) {
		return nodeMap.get(tailMap.get(arc.getId()));
	}

	public NodeImpl getHead(ArcImpl arc) {
		return nodeMap.get(headMap.get(arc.getId()));
	}

	public Iterable<String> getArcLabels() {
		List<String> ret = new ArrayList<String>(edgeMap.size());
		for (ArcImpl e : edgeMap.values()) {
			ret.add(e.getLabel());
		}
		return ret;
	}

	public Collection<ArcImpl> getArcs(NodeImpl node, String label, Direction dir) {
		Set<String> idset;
		if (Direction.INCOMING == dir) {
			idset = incomingMap.get(node.getId());
		} else {
			idset = outgoingMap.get(node.getId());
		}
		List<ArcImpl> result = new ArrayList<ArcImpl>();
		if (idset != null) {
			for (String eid : idset) {
				ArcImpl arc = edgeMap.get(eid);
				if (arc.getLabel().equals(label)) {
					result.add(arc);
				}
			}
		}
		return result;
	}

	public NodeImpl getHomeNode() {
		return nodeMap.get(homeNodeId);
	}

	public NodeImpl getOpposite(NodeImpl node, ArcImpl arc) {
		String hnid = headMap.get(arc.getId());
		String tnid = tailMap.get(arc.getId());

		if (hnid != null && tnid != null) {
			if (hnid.equals(node.getId())) {
				// given node is head so return tail as an opposite
				return nodeMap.get(tnid);
			} else if (tnid.equals(node.getId())) {
				// given node is tail so return head as an opposite
				return nodeMap.get(hnid);
			} else {
				// what is this edge ?
				return null;
			}
		} else {
			// not a node of the graph
			return null;
		}
	}

	public NodeImpl getNodeById(String id) {
		return nodeMap.get(id);
	}

	public String getHomeNodeId() {
		return homeNodeId;
	}

	public void setHomeNodeId(String homeNodeId) {
		this.homeNodeId = homeNodeId;
	}

	public NodeImpl addNode(String id, String label) {
		NodeImpl n = new NodeImpl(id, label);
		nodeMap.put(id, n);
		return n;
	}

	/**
	 * Directed edge from nid1 -> nid2
	 */
	public ArcImpl joinNodes(String nid1, String nid2, String eid, String label) {
		ArcImpl e = new ArcImpl(eid, label);
		edgeMap.put(eid, e);
		headMap.put(eid, nid2);
		tailMap.put(eid, nid1);

		addToOutgoing(nid1, eid);
		addToIncomming(nid2, eid);
		return e;
	}

	public void clear() {
		homeNodeId = null;
		nodeMap.clear();
		edgeMap.clear();
		headMap.clear();
		tailMap.clear();
		incomingMap.clear();
		outgoingMap.clear();
	}

	protected void addToOutgoing(String nid, String eid) {
		Set<String> s = outgoingMap.get(nid);
		if (s == null) {
			s = new HashSet<String>();
			outgoingMap.put(nid, s);
		}
		s.add(eid);
	}

	protected void addToIncomming(String nid, String eid) {
		Set<String> s = incomingMap.get(nid);
		if (s == null) {
			s = new HashSet<String>();
			incomingMap.put(nid, s);
		}
		s.add(eid);
	}
}
