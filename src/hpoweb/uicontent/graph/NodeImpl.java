package hpoweb.uicontent.graph;

import com.vaadin.graph.Node;
import com.vaadin.server.Resource;

class NodeImpl extends GraphElementImpl implements Node {

	private Resource icon;

	public NodeImpl(String id) {
		this(id, id);
	}

	public NodeImpl(String id, String label) {
		super(id, label);
	}

	public Resource getIcon() {
		return icon;
	}

	public void setIcon(Resource icon) {
		this.icon = icon;
	}

}
