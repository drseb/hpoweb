package hpoweb.uicontent.graph;

import com.vaadin.graph.Arc;

class ArcImpl extends GraphElementImpl implements Arc {

    public ArcImpl(String id) {
      this(id, id);
    }

    public ArcImpl(String id, String label) {
      super(id, label);
    }
  }

